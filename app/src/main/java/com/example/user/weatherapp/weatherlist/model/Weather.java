package com.example.user.weatherapp.weatherlist.model;

import java.util.Objects;

public class Weather {
    private String cityName;
    private double temperature;
    private int humidity;
    private double pressure;

    public Weather(String cityName, double temperature, int humidity, double pressure) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }


    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getCityName() {
        return cityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather weather = (Weather) o;
        return Double.compare(weather.temperature, temperature) == 0 &&
                humidity == weather.humidity &&
                Double.compare(weather.pressure, pressure) == 0 &&
                cityName.equals(weather.cityName);
    }

    @Override
    public String toString() {
        return "Weather{" +
                "cityName='" + cityName + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }
}
