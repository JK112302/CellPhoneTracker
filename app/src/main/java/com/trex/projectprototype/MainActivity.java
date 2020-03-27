package com.trex.projectprototype;

import androidx.annotation.NonNull;
import
        androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.trex.projectprototype.main.GPSTracking;
import com.trex.projectprototype.main.registeruser.RegisterUser;

public class MainActivity extends AppCompatActivity {
    private static final long LOCATION_REFRESH_TIME = 5000;
    private static final float LOCATION_REFRESH_DISTANCE = 100;
    int LAUNCH_SECOND_ACTIVITY = 1;
    SharedPreferences sp;
    private static final String TAG = "MainActivity";
    private MusicIntentReceiver myReceiver;
    boolean isHeadphoneConnected;
    AudioManager audioManager;
    ToggleButton stratRide;
    IntentFilter filter;
    private Handler mHandler;
    Location sendSmsLoc;
    LottieAnimationView animCloud, animBike;

    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }
        myReceiver = new MusicIntentReceiver();
        filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        animBike = findViewById(R.id.animation_view_two);
        animCloud = findViewById(R.id.animation_view);
        stratRide = findViewById(R.id.start_ride);
        getSupportActionBar().hide();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean hasData = sp.getBoolean("hasData", false);
        mHandler = new Handler();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                           // Toast.makeText(MainActivity.this, ""+location.getLongitude(), Toast.LENGTH_SHORT).show();
                            sendSmsLoc =location;
                            // Logic to handle location object
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                Log.i("error", "onFailure: "+e);
            }
        });
        if (hasData) {
           // Toast.makeText(this, "" + sp.getString("phone1", "null"), Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(this, RegisterUser.class);
            startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);

        }



        findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopHandler();
            }
        });

        stratRide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    animBike.playAnimation();
                    animBike.setAnimation(R.raw.bikingiscool);
                    animCloud.setAnimation(R.raw.clouds);
                    animCloud.playAnimation();
                    startProgress();
                   // Toast.makeText(MainActivity.this, "checked"+isHeadphoneConnected, Toast.LENGTH_SHORT).show();
                    registerReceiver(myReceiver, filter);

                }
                else{
                    Toast.makeText(getBaseContext(), "Ride Stopped !", Toast.LENGTH_SHORT).show();
                    stopHandler();
                    animBike.setAnimation(R.raw.bikingishard);
                    animCloud.setAnimation(R.raw.clouds);

                }
            }
        });

        findViewById(R.id.clear_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPrefData();
                restartActivity();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(this, "something went wrong !", Toast.LENGTH_SHORT).show();
            }
        }
    }//onActivityResult

    void clearPrefData(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hasData",false);
        editor.putString("phone1", "");
        editor.putString("phone2", "");
        editor.putString("phone3","" );
        editor.putString("phone4", "");
        editor.putString("phone5", "");
        editor.apply();
        Toast.makeText(this, "Data Cleared !", Toast.LENGTH_SHORT).show();
    }

    void restartActivity(){

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private class MusicIntentReceiver extends BroadcastReceiver     {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if(stratRide.isChecked()){
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            isHeadphoneConnected = false;
                            Toast.makeText(context, "EarPhones Unplugged ! ", Toast.LENGTH_SHORT).show();
                            showDilogWithVibration();
                            sendLocationOnSms();
                            break;
                        case 1:
                            isHeadphoneConnected = true;
                            //Toast.makeText(context, "plugged", Toast.LENGTH_SHORT).show();

                            break;
                    }
                }

            }
        }

    }

    private void sendLocationOnSms() {

        sendLocationSMS(sp.getString("phone1",""), sendSmsLoc);
//
        //sendLocationSMS("9910000163",gpsTracking.getLocation());
    }

    private void startProgress() {
        mToastRunnable.run();

    }

    private void stopHandler() {
        mHandler.removeCallbacks(mToastRunnable);
    }

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            if(audioManager.isWiredHeadsetOn()){
                //Toast.makeText(MainActivity.this, "Connected @!", Toast.LENGTH_SHORT).show();
            }
            else {
                showDilogWithVibration();
                Toast.makeText(MainActivity.this, "HeadPhones Not Connected @!", Toast.LENGTH_SHORT).show();

            }

            mHandler.postDelayed(this, 1000*60*5);
        }
    };

    private void showDilogWithVibration() {
        final Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100000);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Warning !")
                .setMessage("Earphones Disconnected !")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Snooze !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        vibrator.cancel();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void sendLocationSMS(String phoneNumber, Location currentLocation) {

        SmsManager smsManager = SmsManager.getDefault();
        StringBuffer smsBody = new StringBuffer();
        smsBody.append("http://maps.google.com?q=");
        smsBody.append(currentLocation.getLatitude());
        smsBody.append(",");
        smsBody.append(currentLocation.getLongitude());
        smsManager.sendTextMessage(phoneNumber, null, smsBody.toString(), null, null);
    }
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS,Manifest.permission.SEND_SMS},
                    123);
        }
    }
}
