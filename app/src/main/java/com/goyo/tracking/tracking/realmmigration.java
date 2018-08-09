package com.goyo.tracking.tracking;

import com.goyo.tracking.tracking.realmmodel.vehiclesettings_model;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class realmmigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            schema.get(vehiclesettings_model.class.getSimpleName())
                    .addField("vtsid", Integer.class);

            oldVersion++;
        }
        if (oldVersion == 1) {
            schema.get(vehiclesettings_model.class.getSimpleName())
                    .removeField("vtsid");

            oldVersion++;
        }


    }
}
