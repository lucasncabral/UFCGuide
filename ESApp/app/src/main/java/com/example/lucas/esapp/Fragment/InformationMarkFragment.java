package com.example.lucas.esapp.Fragment;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.lucas.esapp.Activity.InformationsMarkedActivity;
import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.example.lucas.esapp.R.color.greenbutton;

/**
 * Created by Lucas on 26/07/20
 */
public class InformationMarkFragment extends DialogFragment implements RoutingListener {
    private static LatLng markerSelect;
    private static LatLng myPosition;
    private static GoogleMap map;
    private static MarkedPlace mark;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.accent,R.color.primary,R.color.primary_light,R.color.accent,R.color.primary_dark_material_light};

    public static InformationMarkFragment newInstance(MarkedPlace marker, LatLng position,GoogleMap mapa ) {
        mark = marker;
        markerSelect = new LatLng(marker.getLat(), marker.getLog());
        myPosition = position;
        map = mapa;
        InformationMarkFragment fragment = new InformationMarkFragment();
        return fragment;
    }

    public InformationMarkFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().setTitle("Markedplace information");

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_informations_marketplace, container);

        TextView textViewName = (TextView) view.findViewById(R.id.textMarketName);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageMarket);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        TextView button = (TextView) view.findViewById(R.id.buttonOk);
        FloatingActionButton buttonDrawRoute = (FloatingActionButton) view.findViewById(R.id.btn_draw);
        buttonDrawRoute.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greenbutton)));
        TextView voltar = (TextView) view.findViewById(R.id.textView2);
        polylines = new ArrayList<>();

        textViewName.setText(mark.getName());
        ratingBar.setRating(3.2f);

        button.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                MarkedPlace markedplace = new MarkedPlace(mark.getId(), mark.getCategory(), mark.getName(),mark.getDescricao(), mark.getPhoto(),markerSelect.latitude, markerSelect.longitude,0,mark.getEvaluation() );
                Intent registerIntent = new Intent(getActivity(), InformationsMarkedActivity.class);
                registerIntent.putExtra("markedplace", markedplace);
                startActivity(registerIntent);
            }
        });

        buttonDrawRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRoute();
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
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

    // Second method
    public void  displayRoute() {
        LatLng start = myPosition;
        LatLng end = new LatLng(markerSelect.latitude, markerSelect.longitude);

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();

        map.clear();
        LatLng select = new LatLng(markerSelect.latitude,markerSelect.longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(select).title("");
        map.addMarker(markerOptions);

        for (int i = 0; i < route.size(); i++) {
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            Log.d("Map", "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue());
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

}
