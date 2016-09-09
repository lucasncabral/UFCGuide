package com.example.lucas.esapp.Fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.example.lucas.esapp.Activity.LoginActivity;
import com.example.lucas.esapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Lucas on 05/08/2016.
 */
public class SettingsFragment extends PreferenceFragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, Preference.OnPreferenceClickListener {

    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_settings);

        prefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage((FragmentActivity) getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        PreferenceScreen logOut = (PreferenceScreen) findPreference("log_out");
        logOut.setOnPreferenceClickListener(this);

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals("log_out")){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putBoolean("logado", false);
            ed.commit();

            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        }
        return false;
    }
}
