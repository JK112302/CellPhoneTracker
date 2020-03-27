package com.trex.projectprototype.main.registeruser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trex.projectprototype.R;

public class EnterPhoneNumber extends AppCompatActivity {

    EditText ed1,ed2,ed3,ed4,ed5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_number);
        ed1=findViewById(R.id.ed_1);
        ed2=findViewById(R.id.ed_2);
        ed3=findViewById(R.id.ed_3);
        ed4=findViewById(R.id.ed_4);
        ed5=findViewById(R.id.ed_5);
        getSupportActionBar().hide();
       findViewById(R.id.save_numbers).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              saveData();
           }
       });




    }
    void saveData(){

        String str1,str2,str3,str4,str5;
        str1=ed1.getText().toString();
        str2=ed2.getText().toString();
        str3=ed3.getText().toString();
        str4=ed4.getText().toString();
        str5=ed5.getText().toString();
        if(str1.equals("")||str2.equals("")||str3.equals("")||str4.equals("")||str5.equals("")){
            Toast.makeText(this, "Please Enter 5 Emergency Contacts !", Toast.LENGTH_SHORT).show();
        }
        else{
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("hasData",true);
            editor.putString("phone1", str1);
            editor.putString("phone2", str2);
            editor.putString("phone3", str3);
            editor.putString("phone4", str4);
            editor.putString("phone5", str5);
            editor.apply();
            backToRegister();
        }

    }
    public void backToRegister(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
