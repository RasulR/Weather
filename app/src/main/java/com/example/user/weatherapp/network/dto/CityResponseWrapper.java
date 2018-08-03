package com.example.user.weatherapp.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CityResponseWrapper {
    @SerializedName("predictions")
    private List<MainInfo> mainInfoList;

    public List<MainInfo> getMainInfoList() {
        return mainInfoList;
    }

    public static class MainInfo {
        @SerializedName("structured_formatting")
        private City city;

        public City getCity() {
            return city;
        }

        public static class City {
            @SerializedName("main_text")
            private String name;

            @SerializedName("secondary_text")
            private String country;

            public String getName() {
                return name;
            }

            public String getCountry() {
                return country;
            }

            @Override
            public String toString() {
                return "City{" +
                        "name='" + name + '\'' +
                        ", country='" + country + '\'' +
                        '}';
            }
        }
    }
}
