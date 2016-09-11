package com.example.lucas.esapp.Fragment;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.esapp.Activity.InformationsMarkedActivity;
import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Lucas on 10/09/2016.
 */
public class InformationMarkToAddFragment extends DialogFragment {
    private static String SERVER_URI = "";
    private static LatLng markerSelect;
    private static MarkedPlace mark;
    private ProgressDialog progress;
    private AsyncHttpClient client;

    public static InformationMarkToAddFragment newInstance(MarkedPlace marker) {
        mark = marker;
        markerSelect = new LatLng(marker.getLat(), marker.getLog());

        InformationMarkToAddFragment fragment = new InformationMarkToAddFragment();
        return fragment;
    }

    public InformationMarkToAddFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);

        client = new AsyncHttpClient();
        SERVER_URI = getString(R.string.server_uri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().setTitle("MarkedplaceToAdd information");

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_informations_marketplace_to_add, container);

        TextView textViewName = (TextView) view.findViewById(R.id.textMarketName);
        TextView textViewCategory = (TextView) view.findViewById(R.id.category);
        TextView textViewDescricao = (TextView) view.findViewById(R.id.descircao);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageMarket);
        TextView button = (TextView) view.findViewById(R.id.buttonOk);
        TextView voltar = (TextView) view.findViewById(R.id.textView2);

        textViewName.setText(mark.getName());
        textViewCategory.setText(mark.getCategory());
        textViewDescricao.setText(mark.getDescricao());

        button.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                MarkedPlace markedplace = new MarkedPlace(mark.getId(), mark.getCategory(), mark.getName(),mark.getDescricao(), mark.getPhoto(),markerSelect.latitude, markerSelect.longitude,0,mark.getEvaluation(),mark.getStatus());
                answerPlace(markedplace, "aprovado");
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkedPlace markedplace = new MarkedPlace(mark.getId(), mark.getCategory(), mark.getName(),mark.getDescricao(), mark.getPhoto(),markerSelect.latitude, markerSelect.longitude,0,mark.getEvaluation(),mark.getStatus());
                answerPlace(markedplace, "recusado");
            }
        });

        return view;
    }


    private void answerPlace(MarkedPlace marker, String resposta){
        loading();
        RequestParams requestParams = new RequestParams();
        requestParams.put("idPlace", marker.getId());
        requestParams.put("answer", resposta);
        requestParams.setUseJsonStreamer(true);
        client.post(SERVER_URI + "/addPlaceAccept", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), "Error to send", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("response", responseString);
                Toast.makeText(getActivity(), "Resposta enviada com sucesso", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                closeFragment();
            }
        });
    }

    private void closeFragment() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }



    private void loading() {
        progress = new ProgressDialog(getActivity());

        progress.setMessage(getString(R.string.loading));

        progress.setIndeterminate(false);

        progress.setCancelable(false);

        progress.show();
    }

}
