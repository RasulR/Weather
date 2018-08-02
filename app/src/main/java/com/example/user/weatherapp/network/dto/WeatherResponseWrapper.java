package com.example.user.weatherapp.network.dto;

import com.google.gson.annotations.SerializedName;

public class WeatherResponseWrapper {
    @SerializedName("main")
    private WeatherResponse weather;
    @SerializedName("name")
    private String cityName;

    public WeatherResponse getWeather() {
        return weather;
    }

    public String getCityName() {
        return cityName;
    }

    public static class WeatherResponse {
        @SerializedName("temp")
        private double temperature;
        @SerializedName("humidity")
        private int humidity;
        @SerializedName("pressure")
        private double pressure;

        public double getTemperature() {
            return temperature;
        }

        public int getHumidity() {
            return humidity;
        }

        public double getPressure() {
            return pressure;
        }

        @Override
        public String toString() {
            return "WeatherResponse{" +
                    "temperature=" + temperature +
                    ", humidity=" + humidity +
                    ", pressure=" + pressure +
                    '}';
        }
    }

}
