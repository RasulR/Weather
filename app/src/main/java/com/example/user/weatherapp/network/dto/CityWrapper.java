package com.example.user.weatherapp.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CityWrapper {
    @SerializedName("predictions")
    private List<City> cities;

    public List<City> getCities() {
        return cities;
    }

    public static class City {
        @SerializedName("structured_formatting")
        private String name;

        public String getName() {
            return name;
        }
    }
}
