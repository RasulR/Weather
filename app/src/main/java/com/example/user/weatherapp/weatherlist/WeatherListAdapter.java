package com.example.user.weatherapp.weatherlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.weatherapp.R;
import com.example.user.weatherapp.weatherlist.model.Weather;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.ViewHolder> {

    private List<Weather> weatherList;

    public WeatherListAdapter(List<Weather> weatherList) {
         this.weatherList = weatherList;
    }

    public void setData(List<Weather> weathers) {
        weatherList.clear();
        weatherList.addAll(weathers);
    }

    public List<Weather> getData() {
        return weatherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Weather weather = weatherList.get(position);
        holder.tvCityName.setText(weather.getCityName());
        holder.tvTemp.setText(context.getString(R.string.degree_fmt, weather.getTemperature()));
        holder.tvHumidity.setText(context.getString(R.string.humidity_fmt, weather.getHumidity()));
        holder.tvPressure.setText(context.getString(R.string.pressure_fmt, weather.getPressure()));
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_city)
        TextView tvCityName;
        @BindView(R.id.tv_temp)
        TextView tvTemp;
        @BindView(R.id.tv_humidity)
        TextView tvHumidity;
        @BindView(R.id.tv_pressure)
        TextView tvPressure;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
