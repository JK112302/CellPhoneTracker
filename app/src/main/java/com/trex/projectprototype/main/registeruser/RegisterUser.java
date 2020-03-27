package com.trex.projectprototype.main.registeruser;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trex.projectprototype.R;

import java.util.ArrayList;

public class RegisterUser extends AppCompatActivity {

    int LAUNCH_SECOND_ACTIVITY = 1;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        findViewById(R.id.get_started).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterUser.this, EnterPhoneNumber.class);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });


    }


    @Override
    public void onBackPressed() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(sp.getBoolean("hasData",false)){
            super.onBackPressed();

        }
        else {
            Toast.makeText(this, "Must Enter Emergency Contact !", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToMain(){
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }

        void showRecyclerView(){
            ArrayList<String> dataSet = new ArrayList<>();
            dataSet.add("");
            //recyclerView = findViewById(R.id.recyclerview);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            MyAdapter myAdapter= new MyAdapter(dataSet);
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                //boolean haveData=data.getBooleanExtra("result",false);
                Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
                backToMain();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(this, "something went wrong !", Toast.LENGTH_SHORT).show();
            }
        }
    }//onActivityResult
}
