package com.you.yazan.pharmacyguide;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yazan on 7/10/2016.
 */
public class GetCoordinates extends Activity {
    public static final String PHAR_NAME_LOCATION = "PHAR_NAME_LOCATION";
    public static final String WORKING_HOURS ="WORKING_HOURS";

    String pharmacyName,workingTime;
    ProgressDialog pDialog;
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LANGTITUDE = "langtitude";
    public static final String USER_NAME = "USERNAME";
    public String username;
    JSONArray Coordinates = null;

    Double latitude , langtitude ;
    public static final String LATITUDE = "LATITUDE";
    public static final String LANGTITUDE = "LANGTITUDE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            pharmacyName = extra.getString(PHAR_NAME_LOCATION);
            workingTime = extra.getString(WORKING_HOURS);
            username = extra.getString(USER_NAME);
        }
        if(isOnline()== true)
            getData();
        else
            Toast.makeText(GetCoordinates.this,"No Connection !",Toast.LENGTH_LONG).show();

    }

    public void getData() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(GetCoordinates.this, "please wait", "Loading...");
                pDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {

                // Depends on your web service
                //   httppost.setHeader("Content-type", "application/json");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("pharmacyName", pharmacyName));
                InputStream inputStream = null;
                String result = null;
                try {
                    HttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    HttpPost httppost = new HttpPost("http://yazandd.esy.es/Get_location.php");
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
                Intent intent = new Intent(GetCoordinates.this, MapsActivity.class);
                intent.putExtra(USER_NAME , username);
                intent.putExtra(PHAR_NAME_LOCATION, pharmacyName);
                intent.putExtra(LATITUDE, latitude);
                intent.putExtra(LANGTITUDE, langtitude);
                intent.putExtra(WORKING_HOURS,workingTime);
                finish();
                startActivity(intent);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();

    }


    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            Coordinates = jsonObj.getJSONArray(TAG_RESULTS);
            JSONObject c = Coordinates.getJSONObject(0);
           // latitude = Double.parseDouble(c.getString(TAG_LATITUDE));
          //  langtitude = Double.parseDouble(c.getString(TAG_LANGTITUDE));
            latitude = Double.parseDouble(c.getString(TAG_LATITUDE));
            langtitude = Double.parseDouble(c.getString(TAG_LANGTITUDE));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
