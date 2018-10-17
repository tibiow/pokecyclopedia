package com.example.tibo.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.tibo.map.utilities.Pokemon;

/**
 * Created by tibo on 12/10/18.
 */
public class ChoosePokemonActivity extends AppCompatActivity {

    //private ImageView imageView;
    private Pokemon pokemon;
    private Button btnConfirm;
    private Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_pokemon_activity);

        spinner = (Spinner)findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.pokemon_choices,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("debug", (String) adapterView.getItemAtPosition(i));
                pokemon.setName((String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        imageView = (ImageView)findViewById(R.id.imageView);


        btnConfirm = (Button) findViewById(R.id.buttonConfirm);
        btnConfirm.setOnClickListener(btnConfirmListener);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Bitmap bitmap  = (Bitmap) data.getExtras().get("data");
        //imageView.setImageBitmap(bitmap);
        pokemon = new Pokemon(bitmap);

    }


    private View.OnClickListener btnConfirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.i("debug","coucou je clic");


        }
    };


}