package com.example.user.weatherapp.network.dto;

import com.google.gson.annotations.SerializedName;

public class WeatherWrapper {
    @SerializedName("main")
    private Weather weather;

    public Weather getWeather() {
        return weather;
    }

    public static class Weather {
        @SerializedName("temp")
        private double temperature;
        @SerializedName("humidity")
        private int humidity;
        @SerializedName("pressure")
        private int pressure;

        public double getTemperature() {
            return temperature;
        }

        public int getHumidity() {
            return humidity;
        }

        public int getPressure() {
            return pressure;
        }
    }

}
