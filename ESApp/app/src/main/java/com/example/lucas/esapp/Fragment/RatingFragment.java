package com.example.lucas.esapp.Fragment;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Lucas on 11/08/2016.
 */
public class RatingFragment extends DialogFragment {
    private static MarkedPlace markedPlace;
    private ProgressDialog progress;
    private String SERVER_URI;
    private AsyncHttpClient client;

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
        final RatingBar rating = (RatingBar) view.findViewById(R.id.ratingBar);
        final EditText descricao = (EditText) view.findViewById(R.id.editTextDescricao);
        TextView send = (TextView) view.findViewById(R.id.buttonSend);
        TextView cancel = (TextView) view.findViewById(R.id.cancelbtn);
        SERVER_URI = getString(R.string.server_uri);
        client = new AsyncHttpClient();
        nome.setText(markedPlace.getName());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvaluation(rating.getRating(), String.valueOf(descricao.getText()));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
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


    private void sendEvaluation(double rating, String descricao) {
        loading();
        RequestParams requestParams = new RequestParams();
        requestParams.put("idPlace", markedPlace.getId());
        requestParams.put("nota", rating);
        requestParams.put("descricao", descricao);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        requestParams.put("email", sharedPref.getString("email", "no email"));
        requestParams.setUseJsonStreamer(true);
        client.post(SERVER_URI + "/evaluatePlace", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), "Error to send evaluation", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(getActivity(), "Enviado com sucesso", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                try {
                    this.finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        });
    }

    private void loading() {
        progress = new ProgressDialog(getActivity());

        progress.setMessage(getString(R.string.loading));

        progress.setIndeterminate(false);

        progress.setCancelable(false);

        progress.show();
    }


}
