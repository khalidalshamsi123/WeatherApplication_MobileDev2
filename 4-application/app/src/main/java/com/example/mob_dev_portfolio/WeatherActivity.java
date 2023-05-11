package com.example.mob_dev_portfolio;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mob_dev_portfolio.databinding.ActivityWeatherBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherActivity extends AppCompatActivity {

    private ActivityWeatherBinding binding;

    public double lat,lon;

    private FusedLocationProviderClient fusedLocationProviderClient;

    final ActivityResultLauncher<String> requestLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    //let the user know they cant use this
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        binding.pageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        getCurrentLocation();
        //getInfoVolley(l);
        Log.e("lat : ", String.valueOf(lat));
        Log.e("lon : ", String.valueOf(lon));


        binding.marqueeText2.setSelected(true);
    }

    public void getCurrentLocation() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location l = locationResult.getLastLocation();
                lat = l.getLatitude();
                lon = l.getLongitude();
                getInfoVolley(lat,lon);



            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(singleRequest(Priority.PRIORITY_HIGH_ACCURACY), locationCallback, null);
    }

    private LocationRequest singleRequest(int priority){
        com.google.android.gms.location.LocationRequest locationRequest = new com.google.android.gms.location.LocationRequest.Builder(priority)
                .setWaitForAccurateLocation(true).build();

        return locationRequest;
    }

    public void getInfoVolley(double lat, double lon){
        String latitude = String.valueOf(lat);
        String longitude = String.valueOf(lon);
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+ latitude +"&lon="+ longitude + "&appid=ae84d4709db21078d1f910aa1d71f678";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String cityName;
                        String temp, weatherDes, humidity, maxTemp, minTemp, pressure, wind, icon;

                        try {
                            cityName = response.getString("name");
                            JSONObject mainObject = response.getJSONObject("main");
                            JSONObject windObject = response.getJSONObject("wind");

                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject weatherObject = weatherArray.getJSONObject(0);
                            weatherDes = weatherObject.getString("description");
                            icon = weatherObject.getString("icon");

                            temp = mainObject.getString("temp");
                            humidity = mainObject.getString("humidity");
                            maxTemp = mainObject.getString("temp_max");
                            minTemp = mainObject.getString("temp_min");
                            pressure = mainObject.getString("pressure");
                            wind = windObject.getString("speed");

                        } catch (JSONException e) {
                            cityName = "unknown";
                            weatherDes = "?";
                            temp = "?";
                            humidity = "?";
                            maxTemp = "0";
                            minTemp = "0";
                            pressure = "0";
                            wind = "0";
                            icon = "01d";
                        }
                        double d = Double.parseDouble(temp);
                        int value = (int)d;
                        double newTemp = value - 273;

                        double d2 = Double.parseDouble(maxTemp);
                        double newTempMax = d2 - 273;

                        double d3 = Double.parseDouble(minTemp);
                        double newTempMin = d3 - 273;


                        binding.textCity.setText(cityName);
                        binding.textTemp.setText(String.valueOf(Math.round(newTemp)) + "°C");
                        binding.humidityText.setText(humidity + "%");
                        binding.maxtempText.setText(String.valueOf(Math.round(newTempMax)) + "°C");
                        binding.mintempText.setText(String.valueOf(Math.round(newTempMin)) + "°C");
                        binding.pressureText.setText(pressure + " hPa");
                        binding.windText.setText(wind + " mph");
                        binding.imageView.setImageResource(R.drawable.ic_cloud);
                        binding.textWeather.setText(weatherDes);

                        Picasso.get().load("https://openweathermap.org/img/wn/" + icon + "@2x.png")
                                .placeholder(R.drawable.ic_cloud).into(binding.imageView);


                        double windSpeed = Double.parseDouble(wind);

                        if(windSpeed > 20.0 ){
                            binding.marqueeText2.setText("Weather is not suitable for your plants, get them inside!");
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);

    }
}