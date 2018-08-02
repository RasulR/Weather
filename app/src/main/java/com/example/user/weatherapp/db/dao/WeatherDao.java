package com.example.user.weatherapp.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.user.weatherapp.db.entity.WeatherEntity;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface WeatherDao {

    @Insert
    void insertAll(List<WeatherEntity> weathers);

    @Query("DELETE FROM WeatherEntity")
    void deleteAllWeathers();

    @Query("SELECT * FROM WeatherEntity where fetchedDate >= :lastHourDate")
    Observable<List<WeatherEntity> > getAllWeathersLastHour(long lastHourDate);
}
