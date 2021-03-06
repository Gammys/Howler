package com.example.gouthamishivaprakash.howler;

import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AlarmRealmInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(3000);
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("howler.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
