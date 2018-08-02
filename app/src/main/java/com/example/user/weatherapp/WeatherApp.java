package com.example.user.weatherapp;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.user.weatherapp.db.WeatherDatabase;

public class WeatherApp extends Application {

    private static WeatherApp instance;

    private WeatherDatabase db;

    public static WeatherApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(this, WeatherDatabase.class, "db").build();
    }

    public WeatherDatabase getDb() {
        return db;
    }
}
