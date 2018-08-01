package com.example.user.weatherapp.weatherlist;

import com.example.user.weatherapp.network.dto.WeatherWrapper;

import java.util.List;

public interface WeatherCitiesView {
    void showProgress();
    void showError(String errorMessage);
    void showWeatherList(List<WeatherWrapper.Weather> weathers);
}
