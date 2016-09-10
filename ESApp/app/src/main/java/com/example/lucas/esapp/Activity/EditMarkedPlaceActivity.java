package com.example.lucas.esapp.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class EditMarkedPlaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private MarkedPlace markedplace;

    private EditText editTextName;
    private EditText editTextInfo2;
    private Spinner spinnerCategory;
    private String category;
    private AsyncHttpClient client;
    private String SERVER_URI;
    private ProgressDialog progress;

    private boolean adicionar = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_market_place);


        editTextName = (EditText) findViewById(R.id.editTextName);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Edit Informations");
        SERVER_URI = getString(R.string.server_uri);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
        editTextInfo2 = (EditText) findViewById(R.id.editTextInfo2);

        client = new AsyncHttpClient();

        markedplace = getIntent().getExtras().getParcelable("markedplace");

        if(markedplace.getName().equals("Name")) {
            actionbar.setTitle("Add Location");
            adicionar = true;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pref_syncConnectionTypes_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(this);

        editTextName.setText(markedplace.getName());
        editTextInfo2.setText(markedplace.getDescricao());
        // editTextAddress.setText(markedplace.getAddress());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case 16908332:
                onBackPressed();
                finish();
                return true;
            case R.id.action_register:
                if(adicionar){
                    addLocal();
                } else {
                    // TODO editar informações
                    editLocal();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addLocal() {
        loading();
        RequestParams requestParams = new RequestParams();
        requestParams.put("name", editTextName.getText());
        requestParams.put("category", category);
        requestParams.put("description", editTextInfo2.getText());
        requestParams.put("photo", "no photo");
        requestParams.put("latitude", markedplace.getLat());
        requestParams.put("longitude", markedplace.getLog());
        requestParams.setUseJsonStreamer(true);
        client.post(SERVER_URI + "/addPlace", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
               Toast.makeText(getApplication(), "Error to send place", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(EditMarkedPlaceActivity.this, "Enviado com sucesso", Toast.LENGTH_SHORT).show();
                progress.dismiss();

            }
        });
    }

    private void editLocal() {
        loading();
        RequestParams requestParams = new RequestParams();
        requestParams.put("idPlace", markedplace.getId());
        requestParams.put("name", editTextName.getText());
        requestParams.put("category", category);
        requestParams.put("description", editTextInfo2.getText());
        requestParams.put("photo", "no photo");
        requestParams.setUseJsonStreamer(true);
        client.post(SERVER_URI + "/editPlace", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplication(), "Error to send edit place", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(EditMarkedPlaceActivity.this, "Enviado com sucesso", Toast.LENGTH_SHORT).show();
                progress.dismiss();

            }
        });
    }

    private void loading() {
        progress = new ProgressDialog(this);

        progress.setMessage(getString(R.string.loading));

        progress.setIndeterminate(false);

        progress.setCancelable(false);

        progress.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                category = "Lanchonete";
                break;
            case 1:
                category = "Bloco";
                break;
            case 2:
                category = "Xerox";
                break;
            case 3:
                category = "Coordenação";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
