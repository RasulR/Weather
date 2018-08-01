package com.example.user.weatherapp.network;

import com.example.user.weatherapp.network.dto.CityWrapper;
import com.example.user.weatherapp.network.dto.WeatherWrapper;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("types=(cities)&components=country:kz")
    Observable<CityWrapper> getCityByName(@Query("input") String input);

    @GET("units=metric&lang=ru")
    Observable<WeatherWrapper> getWeatherByCity(@Query("q") String cityName);
}
