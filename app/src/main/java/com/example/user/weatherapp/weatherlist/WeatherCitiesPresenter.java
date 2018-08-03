package com.example.user.weatherapp.weatherlist;

import android.util.Log;

import com.example.user.weatherapp.db.entity.WeatherEntity;
import com.example.user.weatherapp.network.dto.CityResponseWrapper;
import com.example.user.weatherapp.weatherlist.model.Weather;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WeatherCitiesPresenter {
    private static final int HOUR_IN_MS = 1000 * 60 * 60;

    private WeatherCitiesView view;
    private CompositeDisposable disposables;
    private Disposable weatherDisposable;
    private WeatherCitiesInteractor weatherCitiesInteractor;

    private List<Weather> weatherList;

    public WeatherCitiesPresenter(WeatherCitiesView view) {
        this.view = view;
        disposables = new CompositeDisposable();
        weatherList = new ArrayList<>();
        weatherCitiesInteractor = new WeatherCitiesInteractor();
        getAllWeathersLastHour();
    }

    public void findCities(String input) {
        view.showProgress();
        weatherList.clear();
        if (weatherDisposable != null) {
            disposables.delete(weatherDisposable);
        }
        disposables.add(weatherCitiesInteractor.findCityByName(input)
                .takeWhile(cityResponseWrapper -> cityResponseWrapper.getMainInfoList() != null)
                .map(CityResponseWrapper::getMainInfoList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainInfoList -> {
                            Log.e("TAg", "mainfoList = " + mainInfoList);
                            Log.e("TAg", "mainfoList.size() = " + mainInfoList.size());
                            if (mainInfoList.isEmpty()) {
                                view.hideProgress();
                            } else {
                                fetchListWeatherByCity(input, mainInfoList);
                            }
                        },
                        this::onFail));
    }

    private void fetchListWeatherByCity(String input, List<CityResponseWrapper.MainInfo> mainInfoList) {
        weatherDisposable = Observable.fromIterable(mainInfoList)
                .forEach(city -> weatherCitiesInteractor.getWeatherByCityName(city.getCity().getName())
                        .map(weatherResponseWrapper -> new Weather(weatherResponseWrapper.getCityName(),
                                weatherResponseWrapper.getWeather().getTemperature(),
                                weatherResponseWrapper.getWeather().getHumidity(),
                                weatherResponseWrapper.getWeather().getPressure()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(weather -> weatherList.add(weather),
                                this::onFail,
                                () -> {
                                    showWeatherList(weatherList);
                                    saveResponse(input, weatherList);
                                }));
        disposables.add(weatherDisposable);
    }

    public void destroy() {
        view = null;
        disposables.dispose();
    }

    private void onFail(Throwable e) {
        Log.e("TAg", "error when fetch weather", e);
        view.hideProgress();
        view.showError(e.getLocalizedMessage());
    }

    private void showWeatherList(List<Weather> weatherList) {
        Log.e("TAg", "weatherList = " + weatherList);
        Log.e("TAg", "weatherList.size() = " + weatherList.size());
        view.hideProgress();
        view.showWeatherList(weatherList);
    }

    private void saveResponse(String input, List<Weather> weatherList) {
        deleteAllWeathers();
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
                .subscribe(() -> Log.e("TAg", "success save to cache "),
                        e -> Log.e("TAg", "error when save to cache ", e)));
    }

    private void deleteAllWeathers() {
        disposables.add(weatherCitiesInteractor.deleteAllWeathers()
                .subscribeOn(Schedulers.io())
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
                    Log.e("TAg", "getAllWeathersLastHour() savedInput = " + savedInput);
                    Log.e("TAg", "getAllWeathersLastHour() weatherList = " + weatherList.size());
                    if (weatherList != null && !weatherList.isEmpty()) {
                        Log.e("TAg", "getAllWeathersLastHour = " + weatherList);
                        showWeatherList(weatherList);
                    } else {
                        Log.e("TAg", "do request = ");
                        deleteAllWeathers();
                        if (savedInput.length() > 2) {
                            findCities(savedInput);
                        }
                    }
                    return savedInput;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setInputString, e -> Log.e("TAg", "error when retrieve cache ", e),
                        () -> Log.e("TAg", "getAllWeathersLastHour onComplete() ")));
    }

    private void setInputString(String savedInput) {
        Log.e("TAg", "SETINPUTSTRING = " + savedInput);
        view.hideProgress();
        view.listenInputAndSetText(savedInput);
    }
}
