package com.example.user.weatherapp.weatherlist;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.user.weatherapp.WeatherApp;
import com.example.user.weatherapp.db.dao.WeatherDao;
import com.example.user.weatherapp.db.entity.WeatherEntity;
import com.example.user.weatherapp.network.ApiService;
import com.example.user.weatherapp.network.dto.CityResponseWrapper;
import com.example.user.weatherapp.network.dto.WeatherResponseWrapper;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class WeatherCitiesInteractor {

    private static String PREF_INPUT = "pref_input";

    private WeatherDao weatherDao;

    public WeatherCitiesInteractor() {
        weatherDao = WeatherApp.getInstance().getDb().getWeatherDao();
    }

    public Observable<CityResponseWrapper> findCityByName(String input) {
        return ApiService.getApi(true).getCityByName(input);
    }

    public Observable<WeatherResponseWrapper> getWeatherByCityName(String cityName) {
        return ApiService.getApi(false).getWeatherByCity(cityName + ",kz");
    }

    public Long[] insertAllWeathers(List<WeatherEntity> weatherList) {
         return weatherDao.insertAll(weatherList);
    }

    public Completable saveInputString(String input) {
        return Completable.fromAction(() -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherApp.getInstance());
            sharedPreferences.edit().putString(PREF_INPUT, input).apply();
        });
    }

    public Observable<String> getSavedInputString() {
        return Observable.fromCallable(() -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherApp.getInstance());
            return sharedPreferences.getString(PREF_INPUT, "");
        });
    }

    public Completable deleteAllWeathers() {
        return Completable.fromAction(() -> weatherDao.deleteAllWeathers());
    }

    public Single<List<WeatherEntity>> getAllWeathersLastHour(long lastHourDate) {
        return weatherDao.getAllWeathersLastHour(lastHourDate);
    }
}
