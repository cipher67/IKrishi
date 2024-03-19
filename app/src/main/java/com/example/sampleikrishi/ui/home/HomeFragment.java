package com.example.sampleikrishi.ui.home;

import android.content.pm.PackageManager;
import android.location.Location;
import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.sampleikrishi.R;
import com.example.sampleikrishi.databinding.FragmentHomeBinding;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


import com.example.sampleikrishi.ui.slideshow.ImageSliderAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private static final String API_KEY = "91549101f833451d90461651240303";
    private static final String BASE_URL = "https://api.weatherapi.com/v1";

    private static final String CURRENT_WEATHER_ENDPOINT = "/current.json";
    private static final String FORECAST_ENDPOINT = "/forecast.json";
    private Location currentLocation;

    private TextView currentTempTextView, conditionTextView, windTextView, humidityTextView;
    ;

    private ImageView weatherIconImageView;

    private RecyclerView forecastRecyclerView;
    private ForecastAdapter forecastAdapter;
    private List<ForecastItem> forecastItemList;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                currentLocation = locationResult.getLastLocation();
                Log.d("Location", "Latitude: " + currentLocation.getLatitude() + ", Longitude: " + currentLocation.getLongitude());
                getWeatherInfo(currentLocation.getLatitude(), currentLocation.getLongitude());
                getWeatherForecast(currentLocation.getLatitude(), currentLocation.getLongitude());
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        currentTempTextView = root.findViewById(R.id.currentTempTextView);
        conditionTextView = root.findViewById(R.id.conditionTextView);
        windTextView = root.findViewById(R.id.windTextView);
        humidityTextView = root.findViewById(R.id.humidityTextView);
        weatherIconImageView = root.findViewById(R.id.weatherIconImageView);

        forecastRecyclerView = root.findViewById(R.id.forecastRecyclerView);
        forecastItemList = new ArrayList<>();
        forecastAdapter = new ForecastAdapter(forecastItemList, getContext());
        forecastRecyclerView.setAdapter(forecastAdapter);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getCurrentLocation();
        }

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Log.d("Location", "Permission denied");
            }
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void getWeatherInfo(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        String url = BASE_URL + CURRENT_WEATHER_ENDPOINT + "?key=" + API_KEY + "&q=" + location;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject current = response.getJSONObject("current");
                            String tempC = current.getString("temp_c");
                            String conditionText = current.getJSONObject("condition").getString("text");
                            String windKph = current.getString("wind_kph");
                            String humidity = current.getString("humidity");
                            String iconUrl = "https:" + current.getJSONObject("condition").getString("icon");

                            currentTempTextView.setText("Today: " + tempC + "°C");
                            conditionTextView.setText(conditionText);
                            windTextView.setText("Wind: " + windKph + " km/h");
                            humidityTextView.setText("Humidity: " + humidity + "%");

                            loadImage(iconUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(jsonObjectRequest);
    }

    private void getWeatherForecast(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        String url = BASE_URL + FORECAST_ENDPOINT + "?key=" + API_KEY + "&q=" + location + "&days=7";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject forecast = response.getJSONObject("forecast");
                            JSONArray forecastday = forecast.getJSONArray("forecastday");

                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            SimpleDateFormat outputFormatToday = new SimpleDateFormat("EEEE", Locale.getDefault());
                            SimpleDateFormat outputFormatOther = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

                            StringBuilder forecastStr = new StringBuilder();
                            for (int i = 0; i < forecastday.length(); i++) {
                                JSONObject day = forecastday.getJSONObject(i);
                                String dateStr = day.getString("date");
                                Date date = inputFormat.parse(dateStr);
                                String formattedDate;
                                if (i == 0) {
                                    formattedDate = "Today";
                                } else if (i == 1) {
                                    formattedDate = "Tomorrow";
                                } else {
                                    formattedDate = outputFormatOther.format(date);
                                }
                                JSONObject dayInfo = day.getJSONObject("day");
                                String maxTempC = dayInfo.getString("maxtemp_c");
                                String minTempC = dayInfo.getString("mintemp_c");
                                String condition = dayInfo.getJSONObject("condition").getString("text");
                                String iconUrl = "https:" + dayInfo.getJSONObject("condition").getString("icon");
                                String forecastItem = formattedDate + " - Max: " + maxTempC + "°C, Min: " + minTempC + "°C, " + condition + "\n";
                                forecastStr.append(forecastItem);
                                // Add forecast item to the list
                                addForecastItem(formattedDate, maxTempC, minTempC, condition, iconUrl);
                            }

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(jsonObjectRequest);
    }

    private void loadImage(String iconUrl) {
        ImageRequest imageRequest = new ImageRequest(iconUrl,
                new Response.Listener<android.graphics.Bitmap>() {
                    @Override
                    public void onResponse(android.graphics.Bitmap response) {
                        weatherIconImageView.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(imageRequest);
    }

    private void addForecastItem(String formattedDate, String maxTemp, String minTemp, String condition, String iconUrl) {
        forecastItemList.add(new ForecastItem(formattedDate, maxTemp, minTemp, condition, iconUrl));
        forecastAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        fusedLocationClient.removeLocationUpdates(locationCallback);
        binding = null;
    }
}