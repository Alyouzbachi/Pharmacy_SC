package com.you.yazan.pharmacyguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Yazan on 6/8/2016.
 */
public class UserProfile extends Activity {
    TextView welcome;
    Button searchMedicine,nearbyPharmacy,setting;
    public static final String USER_NAME = "USERNAME";
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        welcome = (TextView)findViewById(R.id.TvWelcomeUserprofile);
        searchMedicine = (Button)findViewById(R.id.BtnSearchMed);
        nearbyPharmacy = (Button)findViewById(R.id.BtnNearbyPharmacy);
        setting = (Button)findViewById(R.id.BtnSetting);
        Bundle extra = getIntent().getExtras();
        if(extra != null)
            username = extra.getString(USER_NAME);
        welcome.setText("Welcome "+username);
    }

    public void SearchMedicineActivity(View view) {
        if(isOnline()==false)
            Toast.makeText(UserProfile.this,"No connection !",Toast.LENGTH_LONG).show();
        else {
            Intent intent = new Intent(UserProfile.this, ListViewSearch.class);
            intent.putExtra(USER_NAME , username);
            finish();
            startActivity(intent);
        }
    }

    public void NearbyPharmacyActivity(View view) {
        Intent intent = new Intent(UserProfile.this , NearbyPharmacyMapsActivity.class);
        startActivity(intent);
    }

    public void ProfileSettingActivity(View view) {
        Intent intent = new Intent(UserProfile.this , SettingActivity.class);
        intent.putExtra(USER_NAME,username);
        finish();
        startActivity(intent);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
