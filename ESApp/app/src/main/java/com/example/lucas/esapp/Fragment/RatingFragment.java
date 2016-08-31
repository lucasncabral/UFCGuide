package com.example.lucas.esapp.Fragment;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.esapp.Activity.InformationsMarkedActivity;
import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

/**
 * Created by Lucas on 11/08/2016.
 */
public class RatingFragment extends DialogFragment {
    private static MarkedPlace markedPlace;

    public static RatingFragment newInstance(MarkedPlace marker){
        markedPlace = marker;

        RatingFragment fragment = new RatingFragment();
        return fragment;
    }

    public RatingFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_rating_marker, container);
        TextView nome = (TextView) view.findViewById(R.id.textMarketName);
        RatingBar rating = (RatingBar) view.findViewById(R.id.ratingBar);
        EditText descricao = (EditText) view.findViewById(R.id.editTextDescricao);
        Button send = (Button) view.findViewById(R.id.buttonSend);

        nome.setText(markedPlace.getName());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Send", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().setTitle("Rating");

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


}
