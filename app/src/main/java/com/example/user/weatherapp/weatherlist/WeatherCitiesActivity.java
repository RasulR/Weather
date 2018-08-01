package com.example.user.weatherapp.weatherlist;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.user.weatherapp.R;
import com.example.user.weatherapp.network.dto.WeatherWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherCitiesActivity extends AppCompatActivity implements WeatherCitiesView {

    @BindView(R.id.ed_search)
    EditText edSearch;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private Handler handler;
    private WeatherCitiesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handler = new Handler();
        presenter = new WeatherCitiesPresenter(this);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                if (editable.length() > 2) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.findCities(editable.toString());
                        }
                    }, 300);
                }
            }
        });
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showWeatherList(List<WeatherWrapper.Weather> weathers) {
        progressBar.setVisibility(View.GONE);

    }
}
