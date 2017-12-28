package com.example.pebblesappv2;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by ChunFaiHung on 2017/12/24.
 */

public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();
        Log.d("MyMigration", "Old version: "+oldVersion+", New version: "+newVersion);

        // Migrate to version 1: Add a new class.
        // Example:
        // public Person extends RealmObject {
        //     private String name;
        //     private int age;
        //     // getters and setters left out for brevity
        // }
        if (oldVersion == 1) {
            Log.d("MyMigration", "do nothing essentially");
        }

        // Migrate to version 2: Add a primary key + object references
        // Example:
        // public Person extends RealmObject {
        //     private String name;
        //     @PrimaryKey
        //     private int age;
        //     private Dog favoriteDog;
        //     private RealmList<Dog> dogs;
        //     // getters and setters left out for brevity
        // }
        if (oldVersion == 2) {
            Log.d("MyMigration", "Come on the oldversion is now 2");
        }

        if (newVersion == 3) {
            Log.d("MyMigration", "Come on the oldversion is now 3");
            schema.get("Challenges")
                    .addField("is_recurrent", int.class)
                    .addField("recurrent_period", int.class);
        }
    }
}