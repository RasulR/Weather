package com.example.user.weatherapp.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.user.weatherapp.db.entity.WeatherEntity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface WeatherDao {

    @Insert
    void insertAll(List<WeatherEntity> weathers);

    @Query("DELETE FROM WeatherEntity")
    void deleteAllWeathers();

    @Query("SELECT * FROM WeatherEntity where fetchedDate >= :lastHourDate")
    Single<List<WeatherEntity> > getAllWeathersLastHour(long lastHourDate);

    @Query("SELECT * FROM weatherentity")
    Single<List<WeatherEntity>> getAll();
}
