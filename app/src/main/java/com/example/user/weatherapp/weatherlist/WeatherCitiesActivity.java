package com.example.user.weatherapp.weatherlist;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.user.weatherapp.R;
import com.example.user.weatherapp.weatherlist.model.Weather;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherCitiesActivity extends AppCompatActivity implements WeatherCitiesView,
        TextWatcher {

    @BindView(R.id.rootLayout)
    View rootLayout;
    @BindView(R.id.ed_search)
    EditText edSearch;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private WeatherListAdapter adapter;
    private Handler handler;
    private WeatherCitiesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new WeatherCitiesPresenter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherListAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        handler = new Handler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar.make(rootLayout, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showWeatherList(List<Weather> weathers) {
//        WeatherDiffUtilCallback weatherDiffUtilCallback = new WeatherDiffUtilCallback(adapter.getData(), weathers);
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(weatherDiffUtilCallback);
        adapter.setData(weathers);
        adapter.notifyDataSetChanged();
//        diffResult.dispatchUpdatesTo(adapter);
    }

    @Override
    public void listenInputAndSetText(String savedInput) {
        edSearch.setText(savedInput);
        edSearch.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 2) {
            handler.postDelayed(() -> presenter.findCities(editable.toString()), 300);
        }
    }

    private class WeatherDiffUtilCallback extends DiffUtil.Callback {

        private List<Weather> oldList;

        public WeatherDiffUtilCallback(List<Weather> oldList, List<Weather> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        private List<Weather> newList;

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Weather oldWeather = oldList.get(oldItemPosition);
            Weather newWeather = oldList.get(newItemPosition);
            return oldWeather == newWeather;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Weather oldWeather = oldList.get(oldItemPosition);
            Weather newWeather = oldList.get(newItemPosition);
            return oldWeather.equals(newWeather);
        }
    }
}
