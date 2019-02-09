package com.you.yazan.pharmacyguide;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ListViewSearch extends Activity {

    String SearchMedicine;

    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_PHARMACY_NAME="Pharmacy Name";
    private static final String TAG_ADDRESS="address";
    private static final String TAG_TEL="Tel";
    private static final String TAG_OPEN="Open";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LANGTITUDE = "langtitude";

    public static final String USER_NAME = "USERNAME";
    public static final String DISTANCE = "DISTANCE";
    AutoCompleteTextView searchview;
    GPSTracker gps = new GPSTracker(this);
    public static final String PHAR_NAME_LOCATION = "PHAR_NAME_LOCATION";
    public static final String WORKING_HOURS ="WORKING_HOURS";
    ProgressDialog pDialog;
    public String username;
    JSONArray Medicines = null , names = null;
    Button submitSearch;
    public String[] AutoCompleteMedicineName ;
    ArrayList<HashMap<String, String>> medicineList = new ArrayList<HashMap<String, String>>();

    ArrayList <String> medicineNamesAutoComplete = new ArrayList<String>();
    ListView list;

    double mylatitude,myLongitude,latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_search);
        gps.getLocation();
        if(gps.isGPSEnabled== false)
        {
            gps.showSettingsAlert();
        }
        mylatitude = gps.getLatitude();
        myLongitude = gps.getLongitude();
        Bundle extra = getIntent().getExtras();
        if(extra!=null)
            username = extra.getString(USER_NAME);
        searchview = (AutoCompleteTextView)findViewById(R.id.SvSearch);
        list = (ListView)findViewById(R.id.ListView);
        submitSearch = (Button)findViewById(R.id.BtnSubmitSearch);

        getAutoComplete();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, medicineNamesAutoComplete);
        searchview.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String selected = ((TextView) view.findViewById(R.id.Tv_Lv_PharmacyName)).getText().toString();
                String selectedWorkTime = ((TextView) view.findViewById(R.id.Tv_Lv_Open)).getText().toString();
                Intent intent = new Intent(ListViewSearch.this,GetCoordinates.class);
                intent.putExtra(PHAR_NAME_LOCATION,selected);
                intent.putExtra(WORKING_HOURS,selectedWorkTime);
                intent.putExtra(USER_NAME , username);
                finish();
                startActivity(intent);
            }
        });


    }
    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(ListViewSearch.this, "please wait", "Loading...");
                pDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {

                // Depends on your web service
                //   httppost.setHeader("Content-type", "application/json");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("SearchMedicine", SearchMedicine));
                InputStream inputStream = null;
                String result = null;
                try {
                    //http://yazandd.esy.es/GetPharmacyName.php
                    //http://yazandd.esy.es/Get_Pharmacy_Details.php
                    HttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    HttpPost httppost = new HttpPost("http://yazandd.esy.es/Get_Pharmacy_Details.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

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
            protected void onPostExecute(String result) {
                myJSON = result;
                if (pDialog.isShowing())
                    pDialog.dismiss();
                    showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
    protected void showList(){
        try {
            medicineList.clear();
            JSONObject jsonObj = new JSONObject(myJSON);
            Medicines = jsonObj.getJSONArray(TAG_RESULTS);
                for(int i=0;i<Medicines.length();i++) {
                    JSONObject c = Medicines.getJSONObject(i);
                    String pharmacyName = c.getString(TAG_PHARMACY_NAME);
                    String address = c.getString(TAG_ADDRESS);
                    String tel = c.getString(TAG_TEL);
                    String open = c.getString(TAG_OPEN);
                    latitude = Double.parseDouble(c.getString(TAG_LATITUDE));
                    longitude = Double.parseDouble(c.getString(TAG_LANGTITUDE));

                    HashMap<String, String> Meds = new HashMap<String, String>();

                    Meds.put(TAG_PHARMACY_NAME, pharmacyName);
                    Meds.put(TAG_ADDRESS, address);
                    Meds.put(TAG_TEL, tel);
                    Meds.put(TAG_OPEN, open);
                    String temp = distance();
                    Meds.put(DISTANCE, temp);
                    medicineList.add(Meds);
                }
                Comparator<HashMap<String, String>> distanceComparator = new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> m1, HashMap<String, String> m2) {
                    return m1.get(DISTANCE).compareTo(m2.get(DISTANCE));
                }
            } ;

            Collections.sort((List<HashMap<String, String>>) medicineList, distanceComparator);

            ListAdapter adapter = new SimpleAdapter(
                    ListViewSearch.this, medicineList, R.layout.activity_listview_item,
                    new String[]{TAG_PHARMACY_NAME,TAG_ADDRESS,TAG_TEL,TAG_OPEN,DISTANCE},
                    new int[]{R.id.Tv_Lv_PharmacyName, R.id.Tv_Lv_Address, R.id.Tv_Lv_Tel, R.id.Tv_Lv_Open , R.id.Tv_Lv_Distance}

            );
            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListViewSearch.this , UserProfile.class);
        intent.putExtra(USER_NAME , username);
        finish();
        startActivity(intent);
    }

    public void Search(View view) {

        SearchMedicine = searchview.getText().toString();
        if(isOnline()==false)
            Toast.makeText(ListViewSearch.this, "No connection !", Toast.LENGTH_LONG).show();
        else
            getData();

    }

    public void getAutoComplete(){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(ListViewSearch.this, "please wait", "Loading...");
                pDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                InputStream inputStream = null;
                String result = null;
                try {
                    HttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    HttpPost httppost = new HttpPost("http://yazandd.esy.es/Get_MedicineName_Auto.php");
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
            protected void onPostExecute(String result) {
                myJSON = result;
                if (pDialog.isShowing())
                    pDialog.dismiss();
                readData();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
    protected void readData(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            names = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i=0;i<names.length();i++){
                JSONObject c = names.getJSONObject(i);
                String medName = c.getString("medicine_name");
                medicineNamesAutoComplete.add(medName);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private String distance() {
//Haversine_formula
        double La2 = Math.toRadians(latitude);
        double Lo2 = Math.toRadians(longitude);
        double La1 = Math.toRadians(mylatitude);
        double Lo1 = Math.toRadians(myLongitude);
        double distance = 2 * 6371 * Math.asin(Math.sqrt((Math.pow(Math.sin((La2 - La1) / 2), 2)) + Math.cos(La1) * Math.cos(La2)
                * Math.pow(Math.sin((Lo2 - Lo1) / 2), 2)));

        return (new DecimalFormat("###.####").format(distance));
    }
}
