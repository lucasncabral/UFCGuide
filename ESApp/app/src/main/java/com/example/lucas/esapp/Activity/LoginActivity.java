package com.example.lucas.esapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lucas.esapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final int SIGN_IN_CODE_GOOGLE = 9;
    private GoogleApiClient mGoogleApiClient;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.buttonGooglePlusLogin);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);


        prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(LoginActivity.this, R.string.connectFail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonGooglePlusLogin:
                signIn();
                break;
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_CODE_GOOGLE);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            String name = acct != null ? acct.getDisplayName() : "No name";
            String email = acct != null ? acct.getEmail() : "No email";
            String id = acct.getId();
            String photo = acct != null ? acct.getPhotoUrl().toString() : "No photo";

            try {

                // TODO VALIDAR LOGIN E ENTRAR NA APLICAÇÃO
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);


                // remover

                SharedPreferences.Editor ed = prefs.edit();
                ed.putBoolean("logado", true);
                ed.commit();

                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loginOrRegister(String name, String email, String photo) {
        // String name = request.queryParams("name");
        // String email = request.queryParams("email");
        // String photo = request.queryParams("photo");



    }
}
