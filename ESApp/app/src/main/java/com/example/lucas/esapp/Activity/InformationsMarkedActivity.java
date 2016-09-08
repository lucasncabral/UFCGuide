package com.example.lucas.esapp.Activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lucas.esapp.Fragment.InformationMarkFragment;
import com.example.lucas.esapp.Fragment.RatingFragment;
import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;
import com.google.android.gms.vision.text.Text;

public class InformationsMarkedActivity extends AppCompatActivity {
    private MarkedPlace markedplace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations_marked);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Informations");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);

        markedplace = getIntent().getExtras().getParcelable("markedplace");

        FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.edit_button);
        editButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greenbutton)));
        TextView name = (TextView) findViewById(R.id.textMarketName);
        TextView categoria = (TextView) findViewById(R.id.textCategoria);
        TextView descricao = (TextView) findViewById(R.id.textDescricao);

        name.setText(markedplace.getName());
        categoria.setText(markedplace.getCategory());
        descricao.setText(markedplace.getDescricao());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity();
            }
        });

        Button ratingButton = (Button) findViewById(R.id.rating_button);
        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRating();
            }
        });
    }


    private void fragmentRating() {
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.add(RatingFragment.newInstance(markedplace), null);
        ft.commit();
    }

    private void startEditActivity() {
        Intent registerIntent = new Intent(this, EditMarkedPlaceActivity.class);
        registerIntent.putExtra("markedplace", markedplace);
        startActivity(registerIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case 16908332:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
