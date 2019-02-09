package com.you.yazan.pharmacyguide;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class NearbyPharmacyMapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public GPSTracker gps = new GPSTracker(this);
    public ProgressDialog pDialog ;
    String myJSON;
    private static final String TAG_RESULTS="result";
    private static final String TAG_PHARMACY_NAME="name";
    private static final String TAG_OPEN="open";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LANGTITUDE = "langtitude";
    public double myLongitude , myLatitude ;
    public LatLng me;
    EditText Range;
    Button Search;
    ArrayList<HashMap<String, String>> CoordinatesList = new ArrayList<HashMap<String, String>>();

    JSONArray Coordinates = null;
    double range = 1,temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_pharmacy_maps);
        if (mMap == null)
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        Range = (EditText)findViewById(R.id.EtNearbyPharmaciesRange);
        Search = (Button)findViewById(R.id.BtnNearbyPharmacySubmitRange);
        if(isOnline()== true) {
            getData();
            Location temp = gps.getLocation();
            if(gps.isGPSEnabled == false)
            {
                gps.showSettingsAlert();
            }
            else {
                setMarker();
            }
        }
        else
            Toast.makeText(NearbyPharmacyMapsActivity.this , "No connection !" , Toast.LENGTH_LONG).show();

    }

    private void distance(String name,String open,String la,String lo) {
//Haversine_formula
        double latitude = Double.parseDouble(la);
        double longtitude =Double.parseDouble(lo);
        double La2 = Math.toRadians(latitude); double Lo2 = Math.toRadians(longtitude);
        double La1 = Math.toRadians(myLatitude); double Lo1 = Math.toRadians(myLongitude);
        double distance = 2 * 6371 * Math.asin(Math.sqrt((Math.pow(Math.sin((La2 - La1) / 2), 2)) + Math.cos(La1) * Math.cos(La2)
                * Math.pow(Math.sin((Lo2 - Lo1) / 2), 2)));
        if(distance <= range)
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longtitude)).title(name + " Pharmacy").snippet(open)).showInfoWindow();
        }
      }

    private void setMarker() {
        range = range * 1000;
        myLatitude = gps.getLatitude();
        myLongitude = gps.getLongitude();
        me = new LatLng(myLatitude , myLongitude);
        mMap.addMarker(new MarkerOptions().position(me).title("Me"));
        CameraUpdate center = CameraUpdateFactory.newLatLng(me);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(me)
                .radius(range)
                .strokeColor(Color.RED)
                .fillColor(0x00ADD8E6));
        range = range /1000;
    }

    private void getData() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(NearbyPharmacyMapsActivity.this, "please wait", "Loading...");
                pDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... strings) {
                InputStream inputStream = null;
                String result = null;
                try {
                    HttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    HttpPost httppost = new HttpPost("http://yazandd.esy.es/Get_Pharmacies_Location.php");

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (Exception squish) {
                    }
                }
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s;
                if (pDialog.isShowing())
                    pDialog.dismiss();
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            Coordinates = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i=0;i<Coordinates.length();i++){
                JSONObject c = Coordinates.getJSONObject(i);
                String pharmacyName = c.getString(TAG_PHARMACY_NAME);
                String open = c.getString(TAG_OPEN);
                String latitude = c.getString(TAG_LATITUDE);
                String longtitude = c.getString(TAG_LANGTITUDE);

                distance(pharmacyName,open, latitude,longtitude);
            }
    } catch (JSONException e) {
            e.printStackTrace();
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

    public void ChangeRange(View view) {
        if(Range.getText().toString().isEmpty() || Range.getText().toString().equals(null))
            Toast.makeText(NearbyPharmacyMapsActivity.this , "Please choose range in km.",Toast.LENGTH_LONG).show();
        try {
            range = Double.parseDouble(Range.getText().toString());
        }catch (NumberFormatException e){
            e.printStackTrace();
            Toast.makeText(NearbyPharmacyMapsActivity.this , "Input a number !",Toast.LENGTH_LONG).show();
        }

        ClearMap();
        if(isOnline()== true) {
            getData();
            Location temp = gps.getLocation();
            if(gps.isGPSEnabled == false)
            {
                gps.showSettingsAlert();
            }
            else {
                setMarker();
            }
        }
        else
            Toast.makeText(NearbyPharmacyMapsActivity.this , "No connection !" , Toast.LENGTH_LONG).show();

    }
    public void ClearMap()
    {
        mMap.clear();
    }
}

