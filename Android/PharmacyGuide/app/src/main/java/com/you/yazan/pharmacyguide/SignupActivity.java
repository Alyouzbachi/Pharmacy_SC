package com.you.yazan.pharmacyguide;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

/**
 * Created by Yazan on 6/7/2016.
 */
public class SignupActivity extends Activity {
    private EditText userName,firstName,lastName,Email,Password,BirthDate;
    private RadioGroup pickgender;
    private Button datepicker,register;
    private RadioButton Gendermale,GenderFemale;
    Calendar myCalendar = Calendar.getInstance();
    private ProgressDialog pDialog;
    private static final String TAG = "SignupActivity.java";

    DatePickerDialog.OnDateSetListener date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        intialize();
    }

    public void DatePicker(View view) {


        new DatePickerDialog(SignupActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }



    private void updateLabel() {

        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        BirthDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void intialize()
    {
        userName=(EditText)findViewById(R.id.EtUserNameSignUp);
        firstName=(EditText)findViewById(R.id.EtFirstname);
        lastName=(EditText)findViewById(R.id.EtLastname);
        Email=(EditText)findViewById(R.id.EtEmail);
        Password=(EditText)findViewById(R.id.EtPasswordSignUp);
        BirthDate=(EditText)findViewById(R.id.EtBirthdate);
        pickgender=(RadioGroup)findViewById(R.id.RgGender);
        datepicker=(Button)findViewById(R.id.BtnPickdate);
        register=(Button)findViewById(R.id.BtnRegister);
        GenderFemale=(RadioButton)findViewById(R.id.RbFemale);
        Gendermale=(RadioButton)findViewById(R.id.RbMale);
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

            }
        };

    }

    public void Register(View view) {
        if (isOnline() == true) {
            if(userName.getText().toString().isEmpty() || userName.getText().toString().equals(null))
                Toast.makeText(SignupActivity.this,"Please put username !",Toast.LENGTH_LONG).show();

            else if(firstName.getText().toString().isEmpty() || firstName.getText().toString().equals(null))
                Toast.makeText(SignupActivity.this,"Please put firstname !",Toast.LENGTH_LONG).show();

            else if(lastName.getText().toString().isEmpty() || lastName.getText().toString().equals(null))
                Toast.makeText(SignupActivity.this,"Please put lastname !",Toast.LENGTH_LONG).show();

            else if(Email.getText().toString().isEmpty() || Email.getText().toString().equals(null)||isValidEmail(Email.getText().toString())==false)
                Toast.makeText(SignupActivity.this,"Please put a valid email !",Toast.LENGTH_LONG).show();

            else if(Password.getText().toString().isEmpty() || Password.getText().toString().equals(null))
                Toast.makeText(SignupActivity.this,"Please put password !",Toast.LENGTH_LONG).show();

            else if(BirthDate.getText().toString().isEmpty() || BirthDate.getText().toString().equals(null))
                Toast.makeText(SignupActivity.this,"Please pick you birthdate !",Toast.LENGTH_LONG).show();

            else if(!Gendermale.isChecked() && !GenderFemale.isChecked())
                Toast.makeText(SignupActivity.this,"Please choose gender !",Toast.LENGTH_LONG).show();

            else {
                new PostDataAsyncTask().execute();
                Intent intent = new Intent(SignupActivity.this , LoginActivity.class);
                startActivity(intent);
            }
        }
        else
            Toast.makeText(SignupActivity.this,"No connection !",Toast.LENGTH_LONG).show();
    }

    public class PostDataAsyncTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog =ProgressDialog.show(SignupActivity.this,"please wait","Loading...");
            pDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                    postText();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            Toast.makeText(SignupActivity.this, "Registered", Toast.LENGTH_LONG).show();
            if(pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    // this will post our text data
    private void postText(){
        try{
            // url where the data will be posted
            String postReceiverUrl = "http://yazandd.esy.es/Register.php";
            Log.v(TAG, "postURL: " + postReceiverUrl);

            // HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);

            // add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", userName.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("lastname", lastName.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("firstname", firstName.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", Password.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("email", Email.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("date", BirthDate.getText().toString()));
            if(GenderFemale.isChecked())
                nameValuePairs.add(new BasicNameValuePair("gender","0"));
            if(Gendermale.isChecked())
                nameValuePairs.add(new BasicNameValuePair("gender","1"));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity).trim();
                Log.v(TAG, "Response: " +  responseStr);

                // you can add an if statement here and do other actions based on the response
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}

