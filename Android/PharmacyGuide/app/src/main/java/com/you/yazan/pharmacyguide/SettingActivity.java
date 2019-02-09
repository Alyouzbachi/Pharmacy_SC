package com.you.yazan.pharmacyguide;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class SettingActivity extends Activity {
    EditText username, firstname, password, confirmpassword, birthdate;
    Button update, pickdate;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar = Calendar.getInstance();
    ProgressDialog pDialog;
    public static final String USER_NAME = "USERNAME";
    String oldusername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initialize();
    }

    public void initialize() {
        username = (EditText) findViewById(R.id.EtUserNameEditSetting);
        firstname = (EditText) findViewById(R.id.EtFirstNameEditSetting);
        password = (EditText) findViewById(R.id.EtPasswordEditSetting);
        confirmpassword = (EditText) findViewById(R.id.EtConfirmPasswordEditSetting);
        birthdate = (EditText) findViewById(R.id.EtBirthdateEditSetting);
        update = (Button) findViewById(R.id.BtnUpdateEditSetting);
        pickdate = (Button) findViewById(R.id.BtnPickdateEditSetting);
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

            }
        };
        Bundle extra = getIntent().getExtras();
        if(extra!=null)
            oldusername = extra.getString(USER_NAME);
    }


    public void DatePicker(View view) {

        new DatePickerDialog(SettingActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {

        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthdate.setText(sdf.format(myCalendar.getTime()));
    }

    public void Update(View view) {
        if (isOnline() == true) {
            if (username.getText().toString().isEmpty() || username.getText().toString().equals(null))
                Toast.makeText(SettingActivity.this, "Please put username !", Toast.LENGTH_LONG).show();

            else if (firstname.getText().toString().isEmpty() || firstname.getText().toString().equals(null))
                Toast.makeText(SettingActivity.this, "Please put firstname !", Toast.LENGTH_LONG).show();

            else if (password.getText().toString().isEmpty() || password.getText().toString().equals(null))
                Toast.makeText(SettingActivity.this, "Please put password !", Toast.LENGTH_LONG).show();

            else if (confirmpassword.getText().toString().isEmpty() || confirmpassword.getText().toString().equals(null))
                Toast.makeText(SettingActivity.this, "Please confirm password !", Toast.LENGTH_LONG).show();

            else if (confirmpassword.getText().toString()!= password.getText().toString() && !confirmpassword.getText().toString().equals(password.getText().toString()))
                Toast.makeText(SettingActivity.this , "passwords don't match !.",Toast.LENGTH_LONG).show();

            else if (birthdate.getText().toString().isEmpty() || birthdate.getText().toString().equals(null))
                Toast.makeText(SettingActivity.this, "Please pick you birthdate !", Toast.LENGTH_LONG).show();

            else {
                new PostDataAsyncTask().execute();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        } else
            Toast.makeText(SettingActivity.this, "No connection !", Toast.LENGTH_LONG).show();
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public class PostDataAsyncTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            pDialog = ProgressDialog.show(SettingActivity.this, "please wait", "Loading...");
            pDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                postText();
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(SettingActivity.this, "Updated", Toast.LENGTH_LONG).show();
            if(pDialog.isShowing())
                pDialog.dismiss();
        }
    }
    private void postText(){
        try{
            // url where the data will be posted
            String postReceiverUrl = "http://yazandd.esy.es/Update_user_data.php";

            // HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);

            // add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("oldusername",oldusername));
            nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("firstname", firstname.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("date", birthdate.getText().toString()));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity).trim();

                // you can add an if statement here and do other actions based on the response
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this , UserProfile.class);
        intent.putExtra(USER_NAME , oldusername);
        finish();
        startActivity(intent);
    }
}
