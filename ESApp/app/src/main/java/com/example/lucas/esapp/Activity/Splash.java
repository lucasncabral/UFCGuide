package com.example.lucas.esapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.lucas.esapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Splash extends AppCompatActivity {
    private static final int TIMEOUT_MILLISECONDS = 1000 * 20;
    private String SERVER_URI;
    private SharedPreferences sharedPref;
    private AsyncHttpClient client;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        SERVER_URI = getString(R.string.server_uri);
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        client = new AsyncHttpClient();

        getMarkers();
    }

    private void getMarkers(){
        String getMarkersUrl = SERVER_URI + "/markers";
        client.setConnectTimeout(6000);
        client.get(getMarkersUrl, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                saveMarkers(timeline.toString());
                openLoginScreen();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                super.onFailure(statusCode, headers, throwable, response);
                Toast.makeText(Splash.this, getString(R.string.error_make_request), Toast.LENGTH_LONG).show();
                openLoginScreen();
            }
        });
    }

    private void openLoginScreen() {
        Intent i;
        boolean logado = sharedPref.getBoolean("logado", false);
        if(!logado){
            i = new Intent(Splash.this, LoginActivity.class);
        } else {
            i = new Intent(Splash.this, MainActivity.class);
        }

        startActivity(i);
        finish();
    }

    private void saveMarkers(String timeLine) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("markers", timeLine);
        editor.apply();

    }
}
