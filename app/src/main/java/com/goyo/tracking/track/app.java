package com.goyo.tracking.track;

import android.app.Application;
import android.content.Intent;

import com.goyo.tracking.track.service.livesocketService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class app extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .build();
        //Realm.deleteRealm(realmConfig); // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig);



    }
}
