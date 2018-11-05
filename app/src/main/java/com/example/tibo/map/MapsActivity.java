package com.example.tibo.map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tibo.map.utilities.MyDBHandler;
import com.example.tibo.map.utilities.Pokemon;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements  OnMapReadyCallback, SensorEventListener,LocationListener {

    private static double DELTA_TEMP = 100.0;
    private static double DELTA_LIGHT = 100.0;


    private MapFragment mapFragment;
    private static GoogleMap mMap;
    private Button btnAdd;

    private MyDBHandler mydb = new MyDBHandler(this);

    private SensorManager sensorManager;
    private Sensor thermometer;
    private Sensor lightSensor;
    private LocationManager lm;


    private double localLight;
    private double localTemp;
    private double localLatitude;
    private double localLongitude;
    private  static ArrayList<Marker> markers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Manage button
        btnAdd = (Button) findViewById(R.id.buttonAdd);
        btnAdd.setOnClickListener(btnAddListener);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        thermometer = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        /*
        if (thermometer == null)
            Toast.makeText(this,"thermometer not available on this device",Toast.LENGTH_LONG).show();
*/
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null)
            Toast.makeText(this,"lightSensor not available on this device",Toast.LENGTH_LONG).show();




        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("update","update");
        sensorManager.registerListener(this, thermometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //updateMarkers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomBy(15));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        initializeMarkers();
    }

    private View.OnClickListener btnAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MapsActivity.this, ChoosePokemonActivity.class);
            startActivity(i);
            //Log.i("debug","coucou je clic");
        }
    };

    private void initializeMarkers(){
        Cursor c = mydb.getData();
        Log.i("initialiaze markers putain", String.valueOf(markers.size()));

        if(c.getCount() == 0){
            return;
        }
        while (c.moveToNext()){
            int id = c.getInt(0);
            String name= c.getString(1);
            double latitude = c.getDouble(2);
            double longitude = c.getDouble(3);
            double temp = c.getDouble(4);
            double light = c.getDouble(5);
            String res = String.valueOf(id) + " " + name + " " + String.valueOf(latitude)  + " " + String.valueOf(longitude)  + " " + String.valueOf(temp) + " " + String.valueOf(light);
            Log.i("cursor",res);

            LatLng position = new LatLng(latitude, longitude);
            Marker m = mMap.addMarker(new MarkerOptions().position(position).title(name));
            markers.add(m);

            if((light < localLight - DELTA_LIGHT) || (light > localLight + DELTA_LIGHT) || (temp < localTemp - DELTA_TEMP) || (temp > localTemp + DELTA_TEMP)){
                if(mMap!=null){
                    Log.i("new marker", String.valueOf(latitude));
                    m.setVisible(false);
                }

            }
        }

    }


    private void updateMarkers(){
        Cursor c = mydb.getData();
        if(c.getCount() == 0){
            return;
        }
        if(c.getCount() > markers.size()){

            c.moveToLast();
            int id = c.getInt(0);
            String name= c.getString(1);
            double latitude = c.getDouble(2);
            double longitude = c.getDouble(3);
            double temp = c.getDouble(4);
            double light = c.getDouble(5);
            String res = String.valueOf(id) + " " + name + " " + String.valueOf(latitude)  + " " + String.valueOf(longitude)  + " " + String.valueOf(temp) + " " + String.valueOf(light);
            Log.i("cursor",res);

            LatLng position = new LatLng(latitude, longitude);
            if(mMap!=null) {
                Marker m = mMap.addMarker(new MarkerOptions().position(position).title(name));
                markers.add(m);
                if ((light < localLight - DELTA_LIGHT) || (light > localLight + DELTA_LIGHT) || (temp < localTemp - DELTA_TEMP) || (temp > localTemp + DELTA_TEMP)) {

                    Log.i("new marker", String.valueOf(latitude));
                    m.setVisible(false);
                }
            }
        }
    }

    private void changeMarkersVisibility(){
        int i = 0;
        Cursor c = mydb.getData();
        if(c.getCount() == 0){
            return;
        }

        while (c.moveToNext()){

            int id = c.getInt(0);
            String name= c.getString(1);
            double latitude = c.getDouble(2);
            double longitude = c.getDouble(3);
            double temp = c.getDouble(4);
            double light = c.getDouble(5);
            String res = String.valueOf(id) + " " + name + " " + String.valueOf(latitude)  + " " + String.valueOf(longitude)  + " " + String.valueOf(temp) + " " + String.valueOf(light);

            if((light < localLight - DELTA_LIGHT) || (light > localLight + DELTA_LIGHT) || (temp < localTemp - DELTA_TEMP) || (temp > localTemp + DELTA_TEMP)){
                if(mMap!=null){
                    markers.get(i).setVisible(false);
                }
            }
            else {
                markers.get(i).setVisible(true);
            }
            i++;
        }


    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float value = sensorEvent.values[0];
            //Log.i("coucou la température", String.valueOf(value));
            localTemp = value;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            float value = sensorEvent.values[0];
            //Log.i("coucou la lumière", String.valueOf(value));
            localLight = value;
            //changeMarkersVisibility();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        lm.removeUpdates(this);
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
}
