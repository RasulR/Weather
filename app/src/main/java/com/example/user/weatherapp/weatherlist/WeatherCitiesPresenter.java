package com.example.user.weatherapp.weatherlist;

import android.util.Log;

import com.example.user.weatherapp.db.entity.WeatherEntity;
import com.example.user.weatherapp.network.dto.CityResponseWrapper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

import com.example.user.weatherapp.weatherlist.model.Weather;

public class WeatherCitiesPresenter {
    private static final int HOUR_IN_MS = 1000 * 60;

    private WeatherCitiesView view;
    private CompositeDisposable disposables;
    private Disposable cityAndWeatherDisposable;
    private WeatherCitiesInteractor weatherCitiesInteractor;

    private List<Weather> weatherList;

    public WeatherCitiesPresenter(WeatherCitiesView view) {
        this.view = view;
        disposables = new CompositeDisposable();
        weatherList = new ArrayList<>();
        weatherCitiesInteractor = new WeatherCitiesInteractor();
        getAllWeathersLastHour();
        f();
    }

    public void findCities(String input) {
        view.showProgress();
        weatherList.clear();
        if (cityAndWeatherDisposable != null) {
            disposables.delete(cityAndWeatherDisposable);
        }
        cityAndWeatherDisposable = weatherCitiesInteractor.findCityByName(input)
                .takeWhile(cityResponseWrapper -> cityResponseWrapper.getMainInfoList() != null)
                .map(CityResponseWrapper::getMainInfoList)
                .flatMap(Observable::fromIterable)
                .map(CityResponseWrapper.MainInfo::getCity)
                .flatMap(city -> weatherCitiesInteractor.getWeatherByCityName(city.getName()))
                .map(weatherResponseWrapper -> new Weather(weatherResponseWrapper.getCityName(),
                        weatherResponseWrapper.getWeather().getTemperature(),
                        weatherResponseWrapper.getWeather().getHumidity(),
                        weatherResponseWrapper.getWeather().getPressure()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weather -> {
                            Log.e("TAg", "weather = " + weather);
                            weatherList.add(weather);
                        },
                        this::onFail,
                        () -> {
                            showWeatherList(weatherList);
                            saveResponse(input, weatherList);
                        });
        disposables.add(cityAndWeatherDisposable);
    }

    public void destroy() {
        view = null;
        disposables.dispose();
    }

    private void onFail(Throwable e) {
        Log.e("TAg", "e = " + e.getLocalizedMessage());
        view.hideProgress();
        view.showError(e.getLocalizedMessage());
    }

    private void showWeatherList(List<Weather> weatherList) {
        Log.e("TAg", "weatherList = " + weatherList);
        view.hideProgress();
        view.showWeatherList(weatherList);
    }

    private void saveResponse(String input, List<Weather> weatherList) {
        disposables.add(Observable.fromIterable(weatherList)
                .map(weather -> {
                    WeatherEntity weatherEntity = new WeatherEntity();
                    weatherEntity.setFetchedDate(System.currentTimeMillis());
                    weatherEntity.setCityName(weather.getCityName());
                    weatherEntity.setHumidity(weather.getHumidity());
                    weatherEntity.setTemperature(weather.getTemperature());
                    weatherEntity.setPressure(weather.getPressure());
                    return weatherEntity;
                })
                .toList()
                .map(weatherEntities -> weatherCitiesInteractor.insertAllWeathers(weatherEntities))
                .ignoreElement()
                .andThen(weatherCitiesInteractor.saveInputString(input))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.e("TAg", "success save to cache"),
                        e -> Log.e("TAg", "error when save to cache " + e.getLocalizedMessage())));
    }

    private void deleteAllWeathers() {
        disposables.add(weatherCitiesInteractor.deleteAllWeathers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    private void getAllWeathersLastHour() {
        view.showProgress();
        disposables.add(Observable.zip(
                weatherCitiesInteractor.getAllWeathersLastHour(System.currentTimeMillis() - HOUR_IN_MS)
                        .toObservable()
                        .flatMapIterable(weatherEntities -> weatherEntities)
                        .map(weatherEntity ->
                                new Weather(weatherEntity.getCityName(), weatherEntity.getTemperature(),
                                        weatherEntity.getHumidity(), weatherEntity.getPressure())
                        )
                        .toList()
                        .toObservable(),
                weatherCitiesInteractor.getSavedInputString(),
                (weatherList, savedInput) -> {
                    if (weatherList != null && !weatherList.isEmpty()) {
                        Log.e("TAg", "fromCache = " + weatherList);
                        showWeatherList(weatherList);
                    } else {
                        Log.e("TAg", "go request = " + weatherList);
                        deleteAllWeathers();
                        findCities(savedInput);
                    }
                    return savedInput;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setInputString));
    }

    private void setInputString(String savedInput) {
        view.listenInputAndSetText(savedInput);
    }

    private void f() {
        weatherCitiesInteractor.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherEntities -> {
                    Log.e("TAg", "all from cache = " + weatherEntities);

                }, t -> {
                    Log.e("TAg", "error all = " + t.getLocalizedMessage());
                });
    }
}
