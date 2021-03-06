package com.example.lucas.esapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.soundcloud.android.crop.Crop;


import java.io.File;

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

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int PIC_CROP = 2;
    private static final int RESULT_CAMERA = 3;
    private Uri mSelectedImageURI;
    private Uri profilePhoto;
    private ImageView imagePlace;

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
        imagePlace = (ImageView) findViewById(R.id.imageViewPhoto);

        client = new AsyncHttpClient();

        FloatingActionButton btn_photo = (FloatingActionButton) findViewById(R.id.btn_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


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

    private void selectImage(){
        Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent1, getString(R.string.select_picture)), RESULT_LOAD_IMAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            mSelectedImageURI = data.getData();

            //performCrop(mSelectedImageURI);
            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));

            imagePlace.setImageURI(mSelectedImageURI);
            //Crop.of(mSelectedImageURI, destination).asSquare().start(this);


        }
        else if (resultCode == RESULT_OK && requestCode == Crop.REQUEST_CROP) {

            profilePhoto = Crop.getOutput(data);

            imagePlace.setImageBitmap(null);
            imagePlace.setImageURI(Crop.getOutput(data));

        }
    }
}