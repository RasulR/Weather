package com.example.user.weatherapp.network;

import com.example.user.weatherapp.network.dto.CityResponseWrapper;
import com.example.user.weatherapp.network.dto.WeatherResponseWrapper;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("json?types=(cities)&components=country:kz")
    Observable<CityResponseWrapper> getCityByName(@Query("input") String input);

    @GET("weather?units=metric")
    Observable<WeatherResponseWrapper> getWeatherByCity(@Query("q") String cityName);
}
