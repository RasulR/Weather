package com.example.user.weatherapp.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class WeatherEntity {

    @PrimaryKey
    private String cityName;
    private double temperature;
    private int humidity;
    private double pressure;
    private long fetchedDate;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public long getFetchedDate() {
        return fetchedDate;
    }

    public void setFetchedDate(long fetchedDate) {
        this.fetchedDate = fetchedDate;
    }
}
