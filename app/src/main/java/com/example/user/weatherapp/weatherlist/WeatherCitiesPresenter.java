package com.example.user.weatherapp.weatherlist;

import com.example.user.weatherapp.network.ApiService;
import com.example.user.weatherapp.network.dto.CityWrapper;
import com.example.user.weatherapp.network.dto.WeatherWrapper;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WeatherCitiesPresenter {

    private WeatherCitiesView view;
    private CompositeDisposable disposables;

    public WeatherCitiesPresenter(WeatherCitiesView view) {
        this.view = view;
        disposables = new CompositeDisposable();
    }

    public void findCities(String input) {
        disposables.add(ApiService.getApi(true)
                .getCityByName(input)
                .map(new Function<CityWrapper, List<CityWrapper.City>>() {
                    @Override
                    public List<CityWrapper.City> apply(CityWrapper cityWrapper) throws Exception {
                        return cityWrapper.getCities();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CityWrapper.City>>() {
                               @Override
                               public void accept(List<CityWrapper.City> cities) throws Exception {
                                   onSuccessSearch(cities);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                onFailSearch(throwable);
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        }));
    }

    public void destroy() {
        view = null;
        disposables.dispose();
    }

    private void onSuccessSearch(List<CityWrapper.City> cities) {
        for (CityWrapper.City city : cities) {
            disposables.add(ApiService.getApi(false)
                    .getWeatherByCity(city.getName() + ",kz")
            .map(new Function<WeatherWrapper, WeatherWrapper.Weather>() {
                @Override
                public WeatherWrapper.Weather apply(WeatherWrapper weatherWrapper) throws Exception {
                    return weatherWrapper.getWeather();
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<WeatherWrapper.Weather>() {
                           @Override
                           public void accept(WeatherWrapper.Weather weather) throws Exception {

                           }
                       },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    }));
        }

    }

    private void onSuccessFetchWeather(WeatherWrapper.Weather weather) {

    }

    private void onFailSearch(Throwable e) {
        view.showError(e.getLocalizedMessage());
    }
}
