package com.onur.kryptow.timetravel;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onur.kryptow.timetravel.custom_font.LedFont;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "MainActivity";
    private TextView textview4,textview5;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LedFont tv_timer;
    private boolean pauseTimer = false;
    private int countTimer=0;
    private Double baslangicLatitude,baslangicLongitude,bitisLatitude,bitisLongitude;
    private String locationName;
    private Button button;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //toolbar declare
        setSupportActionBar(toolbar);
      //  toolbar.setBackground(new ColorDrawable(Color.parseColor("#2d9340"))); //toolbard background color
        button = (Button) findViewById(R.id.button);

        mFirebaseAuth = FirebaseAuth.getInstance();  // get user information
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) { //if user is not login,intent the login page
            loadLogInView();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation(); //check whether location service is enable or not in your  phone

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); // floating button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Mail: onuraykut@outlook.com", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    public void baslangic(View v) {
        textview4=findViewById(R.id.textView4);

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Lokasyona isim veriniz");
        final EditText input = new EditText(this);
        b.setView(input);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                locationName = input.getText().toString();  //Get the location name and start timer
                timer();
            }
        });
        b.setNegativeButton("CANCEL", null);
        b.show();
        textview4.setTextColor(Color.RED);
        onStop(); //rebuild for better location data
        onStart();

        if(mLocation!=null) {  // check if it is received or not received
            baslangicLatitude = mLocation.getLatitude();
            baslangicLongitude = mLocation.getLongitude();

        }
        else Toast.makeText(this, "Lokasyon bos", Toast.LENGTH_SHORT).show();
    }

    public void bitis(View v) {
        onStop();
        onStart();
        veritabani_ekle_bit(); //stop the timer and save database
        pauseTimer(null);
        Toast.makeText(this, "Basariyla kaydedildi", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MapsActivity.class); // go to mapsActivity and show map
        i.putExtra("locationName", locationName);
        startActivity(i);
    }
    public void veritabani_ekle_bit() {
        mUserId = mFirebaseUser.getUid();
        if(mLocation!=null){
            bitisLatitude = mLocation.getLatitude();
            bitisLongitude = mLocation.getLongitude();
            String s_time=timeConverter(countTimer);
            Konum konum= new Konum(baslangicLatitude,baslangicLongitude,bitisLatitude,bitisLongitude,s_time,locationName);
            mDatabase.child("users").child(mUserId).child("lokasyon").push().setValue(konum); }
        else Toast.makeText(this, "Lokasyon alinamiyor,konum ayarlarının acik oldugundan emin olunuz", Toast.LENGTH_LONG).show();
        //  Query lastQuery = mDatabase.child("users").child(mUserId).child("lokasyon").orderByKey().limitToLast(1);

    }
    public void savedLocations(View v){  //show saved database
        Intent intent = new Intent(MainActivity.this, SavedLocations.class);
        startActivity(intent);
    }
    public void pauseTimer(View v) {
        textview5=findViewById(R.id.textView5);
        if (pauseTimer == true) {
            pauseTimer = false;
            textview5.setTextColor(Color.BLACK);
        }
        else {pauseTimer = true;
            textview5.setTextColor(Color.RED);
        }

    }

    public void timer() {
        Timer timer = new Timer();
        tv_timer = (LedFont) findViewById(R.id.textView);
        countTimer = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String s_time = String.format("%02d:%02d:%02d",
                                countTimer / 3600,
                                (countTimer % 3600) / 60,
                                countTimer % 60);
                        tv_timer.setText(s_time);
                        if (!pauseTimer) countTimer++;
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

           // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
           // mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

    /*    String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); */
        // You can now create a LatLng Object for use with maps
        //    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


        private String timeConverter(int timeCount){
            String s_time = String.format("%02d:%02d:%02d",
                    countTimer / 3600,
                    (countTimer % 3600) / 60,
                    countTimer % 60);
            return s_time;
        }
    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    public void logOut(View v){
        mFirebaseAuth.signOut();
        loadLogInView();
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}