package com.example.user.weatherapp.weatherlist;

import com.example.user.weatherapp.weatherlist.model.Weather;

import java.util.List;

public interface WeatherCitiesView {
    void showProgress();
    void hideProgress();
    void showError(String errorMessage);
    void showWeatherList(List<Weather> weathers);
    void listenInputAndSetText(String savedInput);
}
