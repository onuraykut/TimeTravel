package com.onur.kryptow.timetravel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    public Context context;
    public String[] locationName;
    public String [] s_time;
    public String[] imagePath;
    public LayoutInflater inflater;

    //ListviewAdapter constructor
    //Gelen değerleri set ediyor
    public ListViewAdapter(Context context, String[] locationName, String[] s_time, String[] imagePath) {
        this.context = context;
        this.locationName = locationName;
        this.s_time=s_time;
        this.imagePath = imagePath;
    }

    @Override
    public int getCount() {
        return locationName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Declare Variables
        TextView locationName_textview;
        TextView countTimer_textview;
        final ImageView imagePath_imageView;
        final ProgressBar progress_image;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.custom_layout, parent, false);//list_item_row dan yeni bir view oluşturuyoruz

        // oluşan itemviewin içindeki alanları Anasayfadan gelen değerler ile set ediyoruz
        locationName_textview= (TextView) itemView.findViewById(R.id.textView16);
        countTimer_textview = (TextView) itemView.findViewById(R.id.textView15);
        imagePath_imageView = (ImageView) itemView.findViewById(R.id.imageView2);
        progress_image=(ProgressBar) itemView.findViewById(R.id.progressBar2);

        locationName_textview.setText(locationName[position]);
        countTimer_textview .setText(s_time[position]);
        Picasso.get().load(imagePath[position]).into(imagePath_imageView,new Callback() {
            @Override
            public void onSuccess() {
                progress_image.setVisibility(View.GONE);
                imagePath_imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
       // imagePath_imageView.setImageResource(sehir_icon_int[position]);
        return itemView;
    }
}