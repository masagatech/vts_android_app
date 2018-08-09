package com.goyo.tracking.tracking;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.cloudinary.android.MediaManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.goyo.tracking.tracking.service.livesocketService;
import com.goyo.tracking.tracking.utils.SHP;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class app extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(2)
                .migration(new realmmigration())
                .build();

//        try {
//            boolean isDeleteRealm = (boolean) SHP.get(this, SHP.ids.isRealmDummped, false);
//            if(!isDeleteRealm) {
//                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
//                if (pInfo.versionCode == 19) {
//                    Realm.deleteRealm(realmConfig);
//                }
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        //Realm.deleteRealm(realmConfig); // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig);

        MediaManager.init(this);

        FacebookSdk.sdkInitialize(getApplicationContext(), 64206);
        AppEventsLogger.activateApp(this);

    }
}
