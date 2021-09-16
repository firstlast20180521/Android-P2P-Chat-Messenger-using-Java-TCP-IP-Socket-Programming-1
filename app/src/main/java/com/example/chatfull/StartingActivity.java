package com.example.chatfull;

import static com.example.chatfull.Utility.printLog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class StartingActivity extends AppCompatActivity {

    private final static String SHARED_PREFERENCES_KEY_USER_SELF = "ME";
    private static String PREFERENCE_FILE_KEY = "SELF_INFO";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_starting);

        printLog("[StartingActivity].onCreate()");

        Intent intent;

        gson = new Gson();
        sharedPref = this.getSharedPreferences(
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String jsonDataString = sharedPref.getString(SHARED_PREFERENCES_KEY_USER_SELF, "");
        //Log.e("START",jsonDataString);
        printLog(String.format("[StartingActivity].onCreate() jsonDataString ===>[%s]", jsonDataString));

        if (jsonDataString.length() > 0) {
            printLog("[StartingActivity].onCreate() Display DialogViewActivity");
            intent = new Intent(this, DialogViewActivity.class);
        }
        else {
            printLog("[StartingActivity].onCreate() Display MainActivity");
            intent = new Intent(this, MainActivity.class);
        }

        printLog("[StartingActivity].onCreate() Calling startActivity");
        startActivity(intent);
        finish();
        printLog("[StartingActivity].onCreate() Closing StartingActivity");
    }
}
