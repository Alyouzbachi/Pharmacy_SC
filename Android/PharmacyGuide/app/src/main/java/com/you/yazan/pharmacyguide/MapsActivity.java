package com.you.yazan.pharmacyguide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static final String LATITUDE = "LATITUDE";
    public static final String LANGTITUDE = "LANGTITUDE";
    public static final String PHAR_NAME_LOCATION = "PHAR_NAME_LOCATION";
    public static final String WORKING_HOURS ="WORKING_HOURS";
    public static final String USER_NAME = "USERNAME";
    public String username;
    int checkItem =0 ;
    String pharmacyName,workingTime;
    double latitude, langtitude;
    public double myLongitude , myLatitude ;
    public GPSTracker gps = new GPSTracker(this);
    LatLng latLng,myPosition;
    boolean drawTrack = false;
    Polyline polyline;
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        Location temp = gps.getLocation();
        if(gps.isGPSEnabled == false)
            gps.showSettingsAlert();
        else {
            setMarker();
        }
    }

    public void setMarker() {
        if (isOnline() == true) try {

            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                username = extra.getString(USER_NAME);
                langtitude = extra.getDouble(LANGTITUDE);
                latitude = extra.getDouble(LATITUDE);
                pharmacyName = extra.getString(PHAR_NAME_LOCATION);
                workingTime = extra.getString(WORKING_HOURS);
            }
            // setUpMapIfNeeded();
            if (langtitude == 0 || latitude == 0 || pharmacyName == null || pharmacyName.equals("")) {
                Toast.makeText(MapsActivity.this, "No connection !", Toast.LENGTH_LONG).show();
            } else {
                latLng = new LatLng(latitude, langtitude);

                myLatitude = gps.getLatitude();
                myLongitude = gps.getLongitude();
                myPosition = new LatLng(myLatitude, myLongitude);

                //for drawing a line between LatLng obejects


                mMap.addMarker(new MarkerOptions().position(myPosition).title("Me"));
                mMap.addMarker(new MarkerOptions().position(latLng).title(pharmacyName + " Pharmacy").snippet(workingTime)).showInfoWindow();

                CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            }

            mMap.setMyLocationEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MapsActivity.this, "Lost Connection !", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivity.this , ListViewSearch.class);
        intent.putExtra(USER_NAME , username);
        finish();
        startActivity(intent);
    }

    public void DrawTrack(View view) {
        if(drawTrack == false) {
            ArrayList<LatLng> locList = new ArrayList<LatLng>();
            locList.add(latLng);
            locList.add(myPosition);
            polyline =
           mMap.addPolyline((new PolylineOptions()).addAll(locList)
                    .width(5)
                    .color(Color.RED)
                    .geodesic(false));
            drawTrack = true;
        }
        else{
            polyline.remove();
            drawTrack = false;

        }
    }

    public void ChangeMapType(View view) {
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
      //  int checkItem = mMap.getMapType()-1;
       // Toast.makeText(MapsActivity.this,"chechitem " +checkItem,Toast.LENGTH_LONG).show();
        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                checkItem=1;
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                checkItem=2;
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                checkItem=3;
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                checkItem=0;
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }
}