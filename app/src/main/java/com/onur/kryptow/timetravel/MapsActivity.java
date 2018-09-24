package com.onur.kryptow.timetravel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.DoubleBuffer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private FirebaseAuth mFirebaseAuth;
    private Location mLocation;
    private LocationManager mLocationManager;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private LatLng baslangicLokasyonu,bitisLokasyonu;
    private String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (savedInstanceState == null) {   //get the location name
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                locationName= null;
            } else {
                locationName= extras.getString("locationName");
            }
        } else {
            locationName= (String) savedInstanceState.getSerializable("locationName");
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mFirebaseUser.getUid();
        if (mFirebaseUser == null) {
            loadLogInView();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final ProgressDialog dialog2=new ProgressDialog(MapsActivity.this);
        dialog2.setMessage("Yukleniyor..");
        dialog2.setCancelable(false);
        dialog2.setInverseBackgroundForced(false);
        dialog2.show();
        mMap = googleMap;

        Query lastQuery = mDatabase.child("users").child(mUserId).child("lokasyon").orderByChild("locationName").equalTo(locationName);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DataSnapshot firstChild1 = snapshot.getChildren().iterator().next().child("baslangicLatitude");
                DataSnapshot firstChild2 = snapshot.getChildren().iterator().next().child("baslangicLongitude");
                DataSnapshot firstChild3 = snapshot.getChildren().iterator().next().child("bitisLatitude");
                DataSnapshot firstChild4 = snapshot.getChildren().iterator().next().child("bitisLongitude");
                // Log.i(TAG,String.valueOf(firstChild1.getValue()));

                Double bbaslangicLatitude=firstChild1.getValue(Double.class);
                Double bbaslangicLongitude=firstChild2.getValue(Double.class);
                Double bbitisLatitude = firstChild3.getValue(Double.class);
                Double bbitisLongitude=firstChild4.getValue(Double.class);

                baslangicLokasyonu= new LatLng(bbaslangicLatitude,bbaslangicLongitude);
                bitisLokasyonu= new LatLng(bbitisLatitude,bbitisLongitude);

                if(mMap!=null) {
                    mMap.addMarker(new MarkerOptions().position(baslangicLokasyonu).title("Başlangıç"));
                    mMap.addMarker(new MarkerOptions().position(bitisLokasyonu).title("Bitiş"));
                    PolylineOptions options=new PolylineOptions().add(baslangicLokasyonu).add(bitisLokasyonu).width(5).color(Color.RED).visible(true);
                    mMap.addPolyline(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bitisLokasyonu,15));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        dialog2.dismiss();

    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
