package com.example.lucas.esapp.Fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.esapp.Activity.EditMarkedPlaceActivity;
import com.example.lucas.esapp.Activity.SettingsActivity;
import com.example.lucas.esapp.Adapter.HistoricAdapter;
import com.example.lucas.esapp.Data.MySQLiteOpenHelper;
import com.example.lucas.esapp.Interface.OnBackPressedListener;
import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lucas on 04/08/2016.
 */
public class NewMapaFragment extends Fragment implements android.location.LocationListener, GoogleMap.OnMapLongClickListener, OnBackPressedListener{

    private static final int LOCATION_REQUEST_CODE = 12;
    private static String SERVER_URI = "";

    private MarkerOptions markerOptions = null;


    private MapView mMapView;
    private GoogleMap mMap;
    private FloatingActionButton mAddPlaceFAB, mLocationFAB;

    private CardView mBottomCardView;
    private Marker clickedMarker;
    private TextView mCidadeTextView, mEnderecoTextView;
    private boolean mBottomBarIsUp;
    private LocationManager mLocationManager;

    private View locationHeight;
    private boolean permissionAsked;

    private LatLng myLocation;
    private MaterialSearchView searchView;
    private ProgressDialog progress;


    private List<MarkedPlace> result;
    private List<MarkedPlace> markedPlaces;


    private static final float DEFAULT_MIN_ZOOM = 4.0f;
    private static final float DEFAULT_MAX_ZOOM = 22.0f;
    private static final LatLngBounds UFCG = new LatLngBounds(
            new LatLng(-7.2170, -35.9111), new LatLng(-7.2105, -35.9056));
    private static final CameraPosition UFCG_CAMERA = new CameraPosition.Builder()
            .target(new LatLng(-7.21517971, -35.90912998)).zoom(16.0f).bearing(0).tilt(0).build();
    private float mMinZoom;
    private float mMaxZoom;

    private ProgressBar mProgressBar;

    private HistoricAdapter searchAdapter;
    private MySQLiteOpenHelper bd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        setHasOptionsMenu(true);


        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);

        if (getActivity() != null) {

            mMapView = (MapView) mRootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();

            mAddPlaceFAB = (FloatingActionButton) mRootView.findViewById(R.id.add_complaint_fab);
            mLocationFAB = (FloatingActionButton) mRootView.findViewById(R.id.location_fab);

            mBottomCardView = (CardView) mRootView.findViewById(R.id.denuncia_card);

            mEnderecoTextView = (TextView) mRootView.findViewById(R.id.login_textView);
            mCidadeTextView = (TextView) mRootView.findViewById(R.id.cidade_textView);

            locationHeight = mRootView.findViewById(R.id.locationYHeight);

            mAddPlaceFAB.setVisibility(View.GONE);

            mBottomCardView.setVisibility(View.GONE);

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        bd = new MySQLiteOpenHelper(getActivity());
        defaultOptionsBd();

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = (String) parent.getItemAtPosition(position);
                searchView.setQuery(query, true);
                searchAdapter.zerarLista();
            }
        });

        searchAdapter = new HistoricAdapter(getActivity(), "lucas123", bd);
        searchView.setAdapter(searchAdapter);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                mMap.clear();
                fetchMarkers();
            }
        });

        return mRootView;
    }

    private void defaultOptionsBd() {
        bd.inserirBusca("lucas123", "Xerox");
        bd.inserirBusca("lucas123", "Lanchonete");
        bd.inserirBusca("lucas123", "Bloco");
        bd.inserirBusca("lucas123", "Coordenação");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(1);

                mProgressBar.setVisibility(View.GONE);

                mMap.setLatLngBoundsForCameraTarget(UFCG);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(UFCG_CAMERA));
                mMap.setMinZoomPreference(mMinZoom);
                mMap.setMaxZoomPreference(mMaxZoom);

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        addComplaint(latLng.latitude, latLng.longitude, true, null);
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if (!marker.equals(clickedMarker)) {

                            if (mBottomBarIsUp) {
                                moveBottomBarDown();
                                mBottomBarIsUp = false;

                                if (clickedMarker != null)
                                    clickedMarker.remove();
                            }

                            seeInformationsMarketPlace(marker);
                            // Intent i = new Intent(getActivity(), DenunciaVisualization.class);
                            // i.putExtra("markerInfo", markersInfo.get(marker));
                            // i.putExtra("userId", userId);
                            // startActivityForResult(i, VISUALIZATION_CODE);
                            // openDenunciaVisualizationDialog(markersInfo.get(marker));
                        }
                        return true;
                    }
                });

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (mBottomBarIsUp) {
                            moveBottomBarDown();
                            mBottomBarIsUp = false;
                        }
                        if (clickedMarker != null)
                            clickedMarker.remove();

                    }
                });

                zoomToLocation();
                fetchMarkers();

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        });
        verifyGpsState();
    }

    private void zoomToLocation() {
        LatLng ufcg = new LatLng(-7.21517971, -35.90912998);
        mMap.clear();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ufcg, 16f));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.clear();
        menuInflater.inflate(R.menu.menu_mapa, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMapView != null)
            mMapView.onResume();

        verifyGpsState();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mMinZoom = DEFAULT_MIN_ZOOM;
        mMaxZoom = DEFAULT_MAX_ZOOM;
    }

    private void fetchMarkers() {
        loading();
        List<MarkedPlace> markers = new ArrayList<>();

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String JSONMarkers = sharedPref.getString("markers", null);

        if(JSONMarkers != null){
            try {
                JSONObject response = new JSONObject(JSONMarkers);
                JSONArray responsePost = response.getJSONArray("result");
                for (int i = 0; i < responsePost.length(); i++) {
                    JSONObject marked = responsePost.getJSONObject(i);
                    Integer id = marked.getInt("id");
                    Double evaluation = marked.getDouble("evaluation");
                    Double distance = marked.getDouble("distance");
                    Double log = marked.getDouble("log");
                    Double lat = marked.getDouble("lat");
                    String name = marked.getString("name");
                    String photo = marked.getString("photo");
                    String category = marked.getString("category");
                    String descricao = marked.getString("descricao");
                    markers.add(new MarkedPlace(id,category,name,descricao,photo,lat,log,distance,evaluation));
                }
        } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        myLocation = getMyLocation();

        DecimalFormat formatter = new DecimalFormat("#0.000");
        double distance;
        for (MarkedPlace marker : markers) {
            distance = distanceUser((new LatLng(marker.getLat(),marker.getLog())))/1000;
            try {
                distance =  Double.valueOf(formatter.format(distance).replace(",", "."));
            } catch (Exception ex){
                ex.printStackTrace();
                distance = -0.001;
            }
            marker.setDistance(distance);

            switch (marker.getCategory()) {
                case "Lanchonete":
                    markerOptions = new MarkerOptions().position(new LatLng(marker.getLat(), marker.getLog())).title(marker.getName());
                    break;
                case "Xerox":
                    markerOptions = new MarkerOptions().position(new LatLng(marker.getLat(), marker.getLog())).title(marker.getName());
                    break;
                case "Coordenação":
                    markerOptions = new MarkerOptions().position(new LatLng(marker.getLat(), marker.getLog())).title(marker.getName());
                    break;
                default:
                    markerOptions = new MarkerOptions().position(new LatLng(marker.getLat(), marker.getLog())).title(marker.getName());
            }
            mMap.addMarker(markerOptions);
        }
        markedPlaces = markers;

        progress.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_complain:
                Intent settingsActivity = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loading() {
        if (getActivity() != null) {
            progress = new ProgressDialog(getActivity());

            progress.setMessage(getString(R.string.loading));

            progress.setIndeterminate(false);

            progress.setCancelable(false);

            progress.show();
        }
    }

    private void verifyGpsState() {
        boolean locationPermission = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (locationPermission) {

            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }
        } else if (!permissionAsked) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);

            permissionAsked = true;

        }


        mLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComplaintInUserLocation(true);
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        addComplaint(latLng.latitude, latLng.longitude, true, null);
    }

    private String[] getAdress(Double latitute, Double longitude) {
        String[] result = {"", "", ""};
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitute, longitude, 1);
            if (addresses.size() > 0) {

                Address address = addresses.get(0);
                result[0] = address.getAddressLine(0);
                result[2] = address.getLocality();
                result[1] = address.getAdminArea();
            } else result[1] = "";

        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    private void addComplaint(final double latitude, final double longitude, boolean isMapClicked, String[] locationInfos) {

        final String[] locations;
        if (locationInfos == null) {
            locations = getAdress(latitude, longitude);

            mEnderecoTextView.setText(locations[0]);
            mCidadeTextView.setText("UFCG");
        } else locations = locationInfos;

        if (clickedMarker != null)
            clickedMarker.remove();


      //  if (locations[1] != null && (locations[1].equals("Paraíba") || locations[1].equals("State of Paraíba"))) {

            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            clickedMarker = mMap.addMarker(markerOptions);
            if (!mBottomBarIsUp) moveBottomBarUp();
            mBottomBarIsUp = true;

            mAddPlaceFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MarkedPlace markedplace = new MarkedPlace(clickedMarker.getPosition().latitude, clickedMarker.getPosition().longitude);
                    Intent registerIntent = new Intent(getActivity(), EditMarkedPlaceActivity.class);
                    registerIntent.putExtra("markedplace", markedplace);
                    startActivity(registerIntent);
                }
            });

            if (!isMapClicked) {
                goToComplaintScreen(locations[0], latitude, longitude);
                moveBottomBarDown();
                mBottomBarIsUp = false;
            }


       // } else {
    //
      //      moveBottomBarDown();
        //    mBottomBarIsUp = false;
          //  final AlertDialog.Builder addComplaintDialog = new AlertDialog.Builder(getActivity());

            //addComplaintDialog.setMessage(getActivity().getResources().getString(R.string.mark_outside_pb));

            //AlertDialog mensagem = addComplaintDialog.create();
           // getContext();

           // mensagem.show();
    //    }
    }

    private void goToComplaintScreen(String location, double latitude, double longitude) {
        // TODO new Place Activity
    }

    private void moveBottomBarUp() {
        mAddPlaceFAB.setVisibility(View.VISIBLE);
        mBottomCardView.setVisibility(View.VISIBLE);
        mLocationFAB.setVisibility(View.GONE);

        ObjectAnimator animX = ObjectAnimator.ofFloat(mBottomCardView, View.TRANSLATION_Y, mBottomCardView.getHeight(), 0);
        int animationDurationTime = 250;
        animX.setDuration(animationDurationTime);
        animX.start();

        // int newLocationButtonHeight = bottomCardHeight + locationButtonHeight + denunciaButtonHeight;

        ObjectAnimator locationAnimX = ObjectAnimator.ofFloat(mLocationFAB, View.TRANSLATION_Y, -locationHeight.getHeight() / 3);
        int locationAnimationDurationTime = 250;
        locationAnimX.setDuration(locationAnimationDurationTime);
        locationAnimX.start();
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.message_gps_off))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void addComplaintInUserLocation(boolean isFromMap) {
        myLocation = getMyLocation();
        if(UFCG.contains(myLocation))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f));
        else
            Toast.makeText(getActivity(), R.string.out_of_ufcg, Toast.LENGTH_SHORT).show();
        if (myLocation == null)
            openMapPermissionDialog();
    }

    private void openMapPermissionDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage(getString(R.string.map_permission_error));
        builder1.setTitle(getString(R.string.map_permission_error_title));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private LatLng getMyLocation() {

        Location first = getLastBestLocation();
        if (first != null)
            return new LatLng(first.getLatitude(), first.getLongitude());

        Location second = getLastKnownLocation();
        if (second != null)
            return new LatLng(second.getLatitude(), second.getLongitude());

        return null;
    }

    private Location getLastKnownLocation() {
        if (mLocationManager == null) return null;
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    private Location getLastBestLocation() {

        Location response = null;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (locationGPS != null) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;

            if (locationNet != null) {
                NetLocationTime = locationNet.getTime();
            }

            if (0 < GPSLocationTime - NetLocationTime) {
                response = locationGPS;
            } else {
                response = locationNet;
            }
        }

        return response;
    }


    private void moveBottomBarDown() {
        mAddPlaceFAB.setVisibility(View.GONE);
        mLocationFAB.setVisibility(View.VISIBLE);
        ObjectAnimator animX = ObjectAnimator.ofFloat(mBottomCardView, View.TRANSLATION_Y, 0, mBottomCardView.getHeight());
        int animationDurationTime = 250;
        animX.setDuration(animationDurationTime);
        animX.start();

        // int newLocationButtonHeight = bottomCardHeight;
        ObjectAnimator locationAnimX = ObjectAnimator.ofFloat(mLocationFAB, View.TRANSLATION_Y, 0);

        int locationAnimationDurationTime = 250;
        locationAnimX.setDuration(locationAnimationDurationTime);
        locationAnimX.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (resultCode == Activity.RESULT_OK) {
        }
    }

    public void seeInformationsMarketPlace(Marker marker) {
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        myLocation = getMyLocation();
        MarkedPlace select = null;
        for(MarkedPlace m : markedPlaces){
            if(m.getLat() == marker.getPosition().latitude && m.getLog() == marker.getPosition().longitude){
                select = m;
                break;
            }
        }
        ft.add(InformationMarkFragment.newInstance(select,myLocation, mMap), null);
        ft.commit();
    }


    private void search(String query) {
        result = new ArrayList<>();
        String originalQuery = query;
        query = query.toUpperCase();
        NumberFormat formatter = new DecimalFormat("#0.000");
        myLocation = getMyLocation();

        for(MarkedPlace marker : markedPlaces){
            if(marker.getName().toUpperCase().contains(query) || marker.getCategory().toUpperCase().equals(query)){
                double distance;
                try {
                    distance = distanceUser(new LatLng(marker.getLat(),marker.getLog()))/1000;
                    Log.d("distance", distance + "");

                    distance =  Double.valueOf(formatter.format(distance).replace(",", "."));
                } catch (Exception ex){
                    ex.printStackTrace();
                    distance = -0.001;
                }

                marker.setDistance(distance);
                result.add(marker);
            }
        }

        if(result.size() > 0){
            bd.inserirBusca("lucas123", originalQuery);
            searchAdapter.update();
        }

        fragmentSearchList(result);
    }

    public void fragmentSearchList(List<MarkedPlace> list){
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        ft.add(ListResultSearchFragment.newInstance(list,this),null);
        ft.commit();
    }

    public void selectInSearch(MarkedPlace marker){
        LatLng select = new LatLng(marker.getLat(),marker.getLog());
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(select, 18f));

        MarkerOptions markerOptions = new MarkerOptions().position(select).title(marker.getName());
        mMap.addMarker(markerOptions);
    }

    private double distanceUser(LatLng marker){
        double result = -1;
        if(myLocation != null){
            // method in stack
            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians(marker.latitude - myLocation.latitude);
            double dLng = Math.toRadians(marker.longitude - myLocation.longitude);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(myLocation.latitude)) * Math.cos(Math.toRadians(marker.latitude)) *
                            Math.sin(dLng/2) * Math.sin(dLng/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double dist = (earthRadius * c);
            result = dist;
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()){
            searchView.closeSearch();

        }

        if (mBottomBarIsUp) {
            moveBottomBarDown();
            mBottomBarIsUp = false;
            if (clickedMarker != null)
                clickedMarker.remove();
        } else{
            mMap.clear();
            fetchMarkers();
        }
    }
}