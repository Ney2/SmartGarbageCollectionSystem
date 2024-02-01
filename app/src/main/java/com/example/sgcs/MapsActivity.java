package com.example.sgcs;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public static final String url = "https://wycliffite-rainbow.000webhostapp.com/notification.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // This is how you can draw the shortest path between two points
        LatLng origin = new LatLng(9.04769706726, 38.7589950562);
        LatLng destination = new LatLng(9.04769706726,38.7589950562 );

        List<LatLng> waypoints = new ArrayList<>();
        List<LatLng> green = new ArrayList<>();
        List<LatLng> yellow = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if(sucess.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String latitude = object.getString("latitude");
                                    String longitude = object.getString("longitude");
                                    String level = object.getString("level");

                                    if(Double.parseDouble(level) >= 70) {
                                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        waypoints.add(latLng);
                                    }

                                    if(Double.parseDouble(level) < 70 && Double.parseDouble(level) >= 40 ) {
                                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        yellow.add(latLng);
                                    }

                                    if(Double.parseDouble(level) < 40 ) {
                                        LatLng positions = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        green.add(positions);
                                    }
                                }
                                drawShortestPath(origin, destination, waypoints, green, yellow);
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void drawShortestPath(LatLng origin, LatLng destination, List<LatLng> waypoints, List<LatLng> green, List<LatLng> yellow) {
        try {
            // Get shortest path from api
            DirectionsResult results = getDirectionsResult(origin, destination, waypoints);
            List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            // Draw the path with polyline
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath)).setColor(Color.BLUE);
            // Zoom camera to origin
            mMap.animateCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 16));
            // Set markers
            mMap.addMarker(new MarkerOptions()
                    .position(origin)
                    .title("Origin")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.building)));

            mMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.building))
            );


            for (LatLng empty : green) {
                mMap.addMarker(new MarkerOptions()
                        .position(empty)
                        .title("Empty")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.green))
                );
            }

            for (LatLng waypoint : waypoints) {
                mMap.addMarker(new MarkerOptions()
                        .position(waypoint)
                        .title("Full")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red))
                );
            }


            for (LatLng medium : yellow) {
                mMap.addMarker(new MarkerOptions()
                        .position(medium)
                        .title("Half Full")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow))
                );
            }

        } catch (ApiException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private DirectionsResult getDirectionsResult(LatLng origin, LatLng destination, List<LatLng> waypoints) throws ApiException, InterruptedException, IOException {
        String apiKey = getString(R.string.directions_api_key);
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));

        if (waypoints != null) {
            List<com.google.maps.model.LatLng> _waypoints = waypoints.stream().map(waypoint ->
                    new com.google.maps.model.LatLng(waypoint.latitude, waypoint.longitude)
            ).collect(Collectors.toList());
            request.waypoints(_waypoints.toArray(new com.google.maps.model.LatLng[0]));

        } else {
            Toast.makeText(getApplicationContext(), "No route to be displayed", Toast.LENGTH_SHORT).show();
        }

        try {
            DirectionsResult result = request.await();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}