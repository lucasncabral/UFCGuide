package com.example.lucas.esapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.lucas.esapp.Fragment.NewMapaFragment;
import com.example.lucas.esapp.Interface.OnBackPressedListener;
import com.example.lucas.esapp.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int NAVIGATION_ICON_SIZE = 24;
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getString(R.string.app_name));

        setSupportActionBar(toolbar);

        Fragment f = new NewMapaFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commit();

        Drawable complaintIcon = getResources().getDrawable(R.drawable.ic_location);
        Drawable menuIcon = complaintIcon.getConstantState().newDrawable().mutate();
        menuIcon.setColorFilter(Color.parseColor("#a39f9f"), PorterDuff.Mode.MULTIPLY);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList){
                if(fragment instanceof OnBackPressedListener){
                    ((OnBackPressedListener)fragment).onBackPressed();
                }
            }
        }
    }

}
