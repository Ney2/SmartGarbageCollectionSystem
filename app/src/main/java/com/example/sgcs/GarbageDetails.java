package com.example.sgcs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import com.example.sgcs.databinding.ActivityPathBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class GarbageDetails extends AppCompatActivity implements OnMapReadyCallback {

    TextView tvid,tvlat,tvlong,tvlevel;
    int position;

    private MapView mapView;

    private GoogleMap mMap;
    private ActivityPathBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_garbage_details);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        getSupportActionBar().setTitle("Garbage Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvid = findViewById(R.id.txtid);
        tvlat = findViewById(R.id.txtlat);
        tvlong = findViewById(R.id.txtlong);
        tvlevel = findViewById(R.id.txtlevel);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));

    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Setting", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences preferences = getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang","");
        setLocale(language);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Intent intent =getIntent();
        position = intent.getExtras().getInt("position");
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(Info.garbageArrayList.get(position).getLatitude(), Info.garbageArrayList.get(position).getLongitude());
        mMap.addMarker(new MarkerOptions().position(location).title(Info.garbageArrayList.get(position).getLevel()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));

        tvid.setText("ID: "+Info.garbageArrayList.get(position).getId());
        tvlat.setText("Latitude: "+Info.garbageArrayList.get(position).getLatitude());
        tvlong.setText("Longitude: "+Info.garbageArrayList.get(position).getLongitude());
        tvlevel.setText("Level: "+Info.garbageArrayList.get(position).getLevel());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}