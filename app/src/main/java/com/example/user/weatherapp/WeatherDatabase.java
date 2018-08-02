package com.example.user.weatherapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.user.weatherapp.db.dao.WeatherDao;
import com.example.user.weatherapp.db.entity.WeatherEntity;

@Database(entities = {WeatherEntity.class}, version = 1)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao getWeatherDao();
}
