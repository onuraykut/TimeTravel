package com.onur.kryptow.timetravel;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SavedLocations extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "SavedLocations";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private TextView textview16;
    ListView mListView;
    ListViewAdapter adapter;
  //  ListViewAdapter adapter; // adapter referansı
    String[] s_time;
    private TypedArray sehir_icon;// iconları tutacak array
    Double[] bbaslangicLatitude;
    Double[] bbaslangicLongitude;
    Double[] bbitisLatitude ;
    Double[] bbitisLongitude ;
    String[] llocationName ;
    String STATIC_MAP_API_ENDPOINT = "http://maps.googleapis.com/maps/api/staticmap?size=200x200&path=color:0xff0000ff|weight:5|";
    String[] imagePath;
    private SwipeRefreshLayout yenileme_nesnesi;
    private ProgressBar progress_image;
    private CircleImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_locations);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      //  toolbar.setBackground(new ColorDrawable(Color.parseColor("#2d9340")));
        yenileme_nesnesi = (SwipeRefreshLayout)findViewById(R.id.yenileme_nesnesi); // nesnemizi tanıttık
        yenileme_nesnesi.setOnRefreshListener(this); // nesnenin bu Class içerinde çalışağını belirttik


        final ProgressDialog dialog2=new ProgressDialog(SavedLocations.this);
        dialog2.setMessage("Yukleniyor..");
        dialog2.setCancelable(false);
        dialog2.setInverseBackgroundForced(false);
        dialog2.show();
        mListView=findViewById(R.id.listview1);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mFirebaseUser.getUid();
        if (mFirebaseUser == null) {
            loadLogInView();
        }
        onRefresh();
        dialog2.dismiss();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SavedLocations.this, MapsActivity.class);
                i.putExtra("locationName", llocationName[position]);
                startActivity(i);

            }
        });

        /* Query lastQuery = mDatabase.child("users").child(mUserId).child("lokasyon").orderByChild("locationName");
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int i=0;
                int count=(int) snapshot.getChildrenCount();
                bbaslangicLatitude=new Double[count];
                bbaslangicLongitude=new Double[count];
                bbitisLatitude=new Double[count];
                bbitisLongitude=new Double[count];
                llocationName=new String[count];
                s_time=new String[count];
                imagePath=new String[count];

               for (DataSnapshot getSnapshot: snapshot.getChildren()) {

                    DataSnapshot firstChild1 = getSnapshot.child("baslangicLatitude");
                    DataSnapshot firstChild2 = getSnapshot.child("baslangicLongitude");
                    DataSnapshot firstChild3 = getSnapshot.child("bitisLatitude");
                    DataSnapshot firstChild4 = getSnapshot.child("bitisLongitude");
                    DataSnapshot firstChild5 = getSnapshot.child("locationName");
                    DataSnapshot firstChild6 = getSnapshot.child("s_time");

                    if(firstChild1.getValue(Double.class)!=null)
                    bbaslangicLatitude[i]= firstChild1.getValue(Double.class); else bbaslangicLatitude[i]=0.0;
                   if(firstChild2.getValue(Double.class)!=null)
                    bbaslangicLongitude[i] = firstChild2.getValue(Double.class); else bbaslangicLongitude[i]=0.0;
                   if(firstChild3.getValue(Double.class)!=null)
                    bbitisLatitude[i] = firstChild3.getValue(Double.class); else bbitisLatitude[i]=0.0;
                   if(firstChild4.getValue(Double.class)!=null)
                    bbitisLongitude[i] = firstChild4.getValue(Double.class); else bbitisLongitude[i]=0.0;
                   if(firstChild5.getValue(String.class)!=null)
                    llocationName[i] = firstChild5.getValue(String.class); else llocationName[i]="";
                   if(firstChild6.getValue(String.class)!=null)
                    s_time [i]= firstChild6.getValue(String.class); else s_time[i]="";


                   imagePath[i] = STATIC_MAP_API_ENDPOINT + bbaslangicLatitude[i] +","+ bbaslangicLongitude[i]+ "|" +bbitisLatitude[i]+","+bbitisLongitude[i]+  "&markers=color%3ablue|label%3aS|" +bbaslangicLatitude[i] +","+ bbaslangicLongitude[i]+ "|" +bbitisLatitude[i]+","+bbitisLongitude[i]+"&sensor=false";

                    i++;
                }
                adapter=new ListViewAdapter(SavedLocations.this,llocationName,s_time,imagePath);
               mListView.setAdapter(adapter);
               dialog2.dismiss();
                //text.setText(myList.get(0)+" "+myList.get(1)+" "+myList.get(10)+"a"+myList.size());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SavedLocations.this, MapsActivity.class);
                i.putExtra("locationName", llocationName[position]);
                startActivity(i);

            }
        });   */


    }
     //   final ListView listView = (ListView) findViewById(R.id.listView);
      //  final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
       // listView.setAdapter(adapter);

    @Override
    public void onRefresh() {
        yenileme_nesnesi.setRefreshing(true);

        Query lastQuery = mDatabase.child("users").child(mUserId).child("lokasyon").orderByChild("locationName");
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int i=0;
                int count=(int) snapshot.getChildrenCount();
                bbaslangicLatitude=new Double[count];
                bbaslangicLongitude=new Double[count];
                bbitisLatitude=new Double[count];
                bbitisLongitude=new Double[count];
                llocationName=new String[count];
                s_time=new String[count];
                imagePath=new String[count];

                for (DataSnapshot getSnapshot: snapshot.getChildren()) {

                    DataSnapshot firstChild1 = getSnapshot.child("baslangicLatitude");
                    DataSnapshot firstChild2 = getSnapshot.child("baslangicLongitude");
                    DataSnapshot firstChild3 = getSnapshot.child("bitisLatitude");
                    DataSnapshot firstChild4 = getSnapshot.child("bitisLongitude");
                    DataSnapshot firstChild5 = getSnapshot.child("locationName");
                    DataSnapshot firstChild6 = getSnapshot.child("s_time");

                    if(firstChild1.getValue(Double.class)!=null)
                        bbaslangicLatitude[i]= firstChild1.getValue(Double.class); else bbaslangicLatitude[i]=0.0;
                    if(firstChild2.getValue(Double.class)!=null)
                        bbaslangicLongitude[i] = firstChild2.getValue(Double.class); else bbaslangicLongitude[i]=0.0;
                    if(firstChild3.getValue(Double.class)!=null)
                        bbitisLatitude[i] = firstChild3.getValue(Double.class); else bbitisLatitude[i]=0.0;
                    if(firstChild4.getValue(Double.class)!=null)
                        bbitisLongitude[i] = firstChild4.getValue(Double.class); else bbitisLongitude[i]=0.0;
                    if(firstChild5.getValue(String.class)!=null)
                        llocationName[i] = firstChild5.getValue(String.class); else llocationName[i]="";
                    if(firstChild6.getValue(String.class)!=null)
                        s_time [i]= firstChild6.getValue(String.class); else s_time[i]="";


                    imagePath[i] = STATIC_MAP_API_ENDPOINT + bbaslangicLatitude[i] +","+ bbaslangicLongitude[i]+ "|" +bbitisLatitude[i]+","+bbitisLongitude[i]+  "&markers=color%3ablue|label%3aS|" +bbaslangicLatitude[i] +","+ bbaslangicLongitude[i]+ "|" +bbitisLatitude[i]+","+bbitisLongitude[i]+"&sensor=false";

                    i++;
                }
                adapter=new ListViewAdapter(SavedLocations.this,llocationName,s_time,imagePath);
                mListView.setAdapter(adapter);
                //text.setText(myList.get(0)+" "+myList.get(1)+" "+myList.get(10)+"a"+myList.size());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });





        yenileme_nesnesi.setRefreshing(false); /* nesnenin yenileme özelliği kapatıldı
         aksi halde sürekli çalışır bu kısmı işleminiz yapılsada yapılmasada kullanın çünkü işlem başarısız olsada
         hata mesajı verirsiniz ama işlem yapılana kadar olan kısımda bu kodu kullanmayın sonrası için kullanın */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        final EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchEditText.setHint("Search");

        searchEditText.setHintTextColor(getResources().getColor(R.color.background));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //run query to the server
                    final ProgressDialog dialog2=new ProgressDialog(SavedLocations.this);
                    dialog2.setMessage("Yukleniyor..");
                    dialog2.setCancelable(false);
                    dialog2.setInverseBackgroundForced(false);
                    dialog2.show();
                    String search=searchEditText.getText().toString();

                    Query lastQuery = mDatabase.child("users").child(mUserId).child("lokasyon").orderByChild("locationName").startAt(search)
                            .endAt(search+"\uf8ff");
                    lastQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            int i=0;
                            int count=(int) snapshot.getChildrenCount();
                            bbaslangicLatitude=new Double[count];
                            bbaslangicLongitude=new Double[count];
                            bbitisLatitude=new Double[count];
                            bbitisLongitude=new Double[count];
                            llocationName=new String[count];
                            s_time=new String[count];
                            imagePath=new String[count];

                            for (DataSnapshot getSnapshot: snapshot.getChildren()) {

                                DataSnapshot firstChild1 = getSnapshot.child("baslangicLatitude");
                                DataSnapshot firstChild2 = getSnapshot.child("baslangicLongitude");
                                DataSnapshot firstChild3 = getSnapshot.child("bitisLatitude");
                                DataSnapshot firstChild4 = getSnapshot.child("bitisLongitude");
                                DataSnapshot firstChild5 = getSnapshot.child("locationName");
                                DataSnapshot firstChild6 = getSnapshot.child("s_time");

                                if(firstChild1.getValue(Double.class)!=null)
                                    bbaslangicLatitude[i]= firstChild1.getValue(Double.class); else bbaslangicLatitude[i]=0.0;
                                if(firstChild2.getValue(Double.class)!=null)
                                    bbaslangicLongitude[i] = firstChild2.getValue(Double.class); else bbaslangicLongitude[i]=0.0;
                                if(firstChild3.getValue(Double.class)!=null)
                                    bbitisLatitude[i] = firstChild3.getValue(Double.class); else bbitisLatitude[i]=0.0;
                                if(firstChild4.getValue(Double.class)!=null)
                                    bbitisLongitude[i] = firstChild4.getValue(Double.class); else bbitisLongitude[i]=0.0;
                                if(firstChild5.getValue(String.class)!=null)
                                    llocationName[i] = firstChild5.getValue(String.class); else llocationName[i]="";
                                if(firstChild6.getValue(String.class)!=null)
                                    s_time [i]= firstChild6.getValue(String.class); else s_time[i]="";


                                imagePath[i] = STATIC_MAP_API_ENDPOINT + bbaslangicLatitude[i] +","+ bbaslangicLongitude[i]+ "|" +bbitisLatitude[i]+","+bbitisLongitude[i]+  "&markers=color%3ablue|label%3aS|" +bbaslangicLatitude[i] +","+ bbaslangicLongitude[i]+ "|" +bbitisLatitude[i]+","+bbitisLongitude[i]+"&sensor=false";

                                i++;
                            }
                            adapter=new ListViewAdapter(SavedLocations.this,llocationName,s_time,imagePath);
                            mListView.setAdapter(adapter);
                            dialog2.dismiss();
                            //text.setText(myList.get(0)+" "+myList.get(1)+" "+myList.get(10)+"a"+myList.size());

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Failed to read value
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });

                }
                return false;
            }
        });


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    } */
}
