package com.example.lucas.esapp.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditMarkedPlaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private MarkedPlace markedplace;

    private EditText editTextName;
    private EditText editTextInfo2;
    private Spinner spinnerCategory;

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
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
        editTextInfo2 = (EditText) findViewById(R.id.editTextInfo2);

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
                // TODO editar informações
                if(adicionar){
                    Toast.makeText(EditMarkedPlaceActivity.this, "Add", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditMarkedPlaceActivity.this, "Edit", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(EditMarkedPlaceActivity.this, position + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
