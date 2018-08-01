package com.example.user.weatherapp.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static final String PLACE_ENDPOINT = "https://maps.googleapis.com/maps/api/place/autocomplete/";
    private static final String WEATHER_ENDPOINT = "http://api.openweathermap.org/data/2.5/";
    private static final String PLACE_API_KEY = "AIzaSyAJI-ukD17BKPMkgyxMpvqeC0989fsu4n4";
    private static final String WEATHER_API_KEY = "97e3af460c0f73bdedc8a1f25f6dde7f";
    private static final int CONNECT_TIMEOUT_IN_MS = 10000;

    private static Api placeApi = initApi(true);
    private static Api weatherApi = initApi(false);

    public static Api getApi(boolean findPlace) {
        return findPlace ? placeApi : weatherApi;
    }

    private ApiService() {

    }

    private static Api initApi(boolean findPlace) {
        String baseUrl;
        final String queryParameter;
        final String apiKey;
        if (findPlace) {
            baseUrl = PLACE_ENDPOINT;
            queryParameter = "key";
            apiKey = PLACE_API_KEY;
        } else {
            baseUrl = WEATHER_ENDPOINT;
            queryParameter = "APPID";
            apiKey = WEATHER_API_KEY;
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor requestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter(queryParameter, apiKey)
                        .build();

                Request request = original.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        };
        OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(requestInterceptor)
                .build();

        return new Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(Api.class);
    }
}
