package com.goyo.tracking.tracking.info;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.goyo.tracking.tracking.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {

    Integer VersionCode;
    String VersionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Element adsElement = new Element();
        adsElement.setTitle("GoYo VTS");
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            VersionCode = pInfo.versionCode;
            VersionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.goyologo500)
                .setDescription("We are Technology Enthusiast having broad range of experience in Vehicles Tracking, rides bookings & deliveries")
                .addItem(adsElement)
                .addItem(new Element().setTitle("Version : " + VersionName + " (" + VersionCode + ")"))
                .addGroup("Discover what all the buzz is about!")
                .addEmail("info@goyo.in")
                .addWebsite("http://goyo.in/")
                .addFacebook("GoYoMobileApp")
//                .addTwitter("medyo80")
//                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.goyo.tracking.tracking")
//                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);

        setTitle("About Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format("Privacy Policy", Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_link);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(About.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu
        switch (item.getItemId()) {
            //When home is clicked
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
