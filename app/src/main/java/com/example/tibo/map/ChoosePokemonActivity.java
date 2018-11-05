package com.example.tibo.map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tibo.map.utilities.MyDBHandler;
import com.example.tibo.map.utilities.Pokemon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by tibo on 12/10/18.
 */
public class ChoosePokemonActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    //private ImageView imageView;
    private Button btnConfirm;
    private Spinner spinner;
    ArrayAdapter<CharSequence> adapter;


    private LocationManager lm;
    private SensorManager sensorManager;
    private Sensor thermometer;
    private Sensor lightSensor;
    private MyDBHandler mydb;


    private double currentTemp;
    private double currentLight;
    private double currentLatitude ;
    private double currentLongitude ;
    private String currentPokemon;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.mydb = new MyDBHandler(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_pokemon_activity);

        //Manage Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        thermometer = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        /*
        if (thermometer == null)
            Toast.makeText(this, "thermometer not available on this device", Toast.LENGTH_LONG).show();
            */
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null)
            Toast.makeText(this, "lightSensor not available on this device", Toast.LENGTH_LONG).show();

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.pokemon_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("debug", (String) adapterView.getItemAtPosition(i));
                //pokemon.setName((String) adapterView.getItemAtPosition(i));
                currentPokemon = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DisplayMetrics disp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(disp);
        getWindow().setLayout((int) (0.6 * disp.widthPixels), (int) (disp.heightPixels * 0.6));


        btnConfirm = (Button) findViewById(R.id.buttonConfirm);
        btnConfirm.setOnClickListener(btnConfirmListener);


        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }


    private View.OnClickListener btnConfirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            Log.i("debug","coucou je clic");
            Log.i("templight","temp =" + currentTemp + " and light = "+ currentLight + "\n");
            Log.i("coord","lat =" + currentLatitude+ " and long = "+ currentLongitude + "\n");
            mydb.addData(currentPokemon,currentLatitude,currentLongitude,currentTemp,currentLight);

            Log.i("test", String.valueOf(mydb.getData().getCount()));
            Intent i = new Intent( ChoosePokemonActivity.this ,MapsActivity.class);
            startActivity(i);


        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, thermometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (lm != null) {
            lm.removeUpdates((this));
        }
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i("location", String.valueOf(location.getLatitude()));
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float value = sensorEvent.values[0];
            currentTemp = value;
            //Log.i("coucou la température", String.valueOf(currentTemp));

        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            float value = sensorEvent.values[0];
            currentLight = value;
            //Log.i("coucou la lumière", String.valueOf(currentLight));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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