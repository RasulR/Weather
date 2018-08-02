package com.example.user.weatherapp.weatherlist;

import android.util.Log;

import com.example.user.weatherapp.db.entity.WeatherEntity;
import com.example.user.weatherapp.network.dto.CityResponseWrapper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

import com.example.user.weatherapp.weatherlist.model.Weather;

public class WeatherCitiesPresenter {
    private static final int HOUR_IN_MS = 1000 * 60 * 60;

    private WeatherCitiesView view;
    private CompositeDisposable disposables;
    private WeatherCitiesInteractor weatherCitiesInteractor;

    private List<Weather> weatherList;

    public WeatherCitiesPresenter(WeatherCitiesView view) {
        this.view = view;
        disposables = new CompositeDisposable();
        weatherList = new ArrayList<>();
        weatherCitiesInteractor = new WeatherCitiesInteractor();
    }

    public void findCities(String input) {
        view.showProgress();
        weatherList.clear();
        disposables.clear();
        disposables.add(weatherCitiesInteractor.findCityByName(input)
                .map(CityResponseWrapper::getMainInfoList)
                .flatMap(mainInfoList -> {
                    Log.e("TAg", "cities = " + mainInfoList);
                    return Observable.fromIterable(mainInfoList);
                })
                .map(CityResponseWrapper.MainInfo::getCity)
                .flatMap(city -> weatherCitiesInteractor.getWeatherByCityName(city.getName()))
                .map(weatherResponseWrapper -> new Weather(weatherResponseWrapper.getCityName(),
                        weatherResponseWrapper.getWeather().getTemperature(),
                        weatherResponseWrapper.getWeather().getHumidity(),
                        weatherResponseWrapper.getWeather().getPressure()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        weather -> {
                            Log.e("TAg", "weather = " + weather);
                            weatherList.add(weather);
                        },
                        this::onFail,
                        () -> {
                            showWeatherList(weatherList);
                            saveResponse(input, weatherList);
                        })
        );
    }

    public void destroy() {
        view = null;
        disposables.dispose();
    }

    private void onFail(Throwable e) {
        Log.e("TAg", "e = " + e.getLocalizedMessage());
        view.showError(e.getLocalizedMessage());
    }

    private void showWeatherList(List<Weather> weatherList) {
        Log.e("TAg", "weatherList = " + weatherList);
        view.showWeatherList(weatherList);
    }

    private void saveResponse(String input, List<Weather> weatherList) {
        List<WeatherEntity> list = new ArrayList<>();
        for (Weather weather : weatherList) {
            WeatherEntity weatherEntity = new WeatherEntity();
            weatherEntity.setFetchedDate(System.currentTimeMillis());
            weatherEntity.setCityName(weather.getCityName());
            weatherEntity.setHumidity(weather.getHumidity());
            weatherEntity.setTemperature(weather.getTemperature());
            weatherEntity.setPressure(weather.getPressure());
            list.add(weatherEntity);
        }
        disposables.add(
                weatherCitiesInteractor.insertAllWeathers(list)
                        .andThen(weatherCitiesInteractor.saveInputString(input))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
    }

    public void deleteAllWeathers() {
        disposables.add(weatherCitiesInteractor.deleteAllWeathers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void getAllWeathersLastHour() {
        disposables.add(
                Observable.zip(
                        weatherCitiesInteractor.getAllWeathersLastHour(System.currentTimeMillis() - HOUR_IN_MS)
                                .flatMapIterable(weatherEntities -> weatherEntities)
                                .map(weatherEntity ->
                                        new Weather(weatherEntity.getCityName(), weatherEntity.getTemperature(),
                                                weatherEntity.getHumidity(), weatherEntity.getPressure())
                                )
                                .toList()
                                .toObservable(),
                        weatherCitiesInteractor.getSavedInputString(),
                        (weatherEntities, savedInput) -> {
                            if (weatherEntities != null && !weatherEntities.isEmpty()) {
                                showWeatherList(weatherEntities);
                            }
                            return savedInput;
                        })
                        .subscribe(this::setInputString));
    }

    private void setInputString(String savedInput) {

    }
}
