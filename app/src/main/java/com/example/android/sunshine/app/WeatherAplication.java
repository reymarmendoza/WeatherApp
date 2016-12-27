package com.example.android.sunshine.app;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Created by reyma on 24/12/2016.
 */

public class WeatherAplication extends MainActivity{
/*
    public void validacionDePermisosSegunVersion() {
        //validacion de permisos MARSHMALLOW en adelante
        if(Build.VERSION.SDK_INT  > 22){
            //valida si el permiso GPS NO fue otorgado por la solicitud que se hace en el manifest
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //shouldShowRequestPermissionRationale no lo uso porque parece redundate a checkSelfPermission
                solicitarPermisoAlUsuario();
            }
        }
    }
    //solicita permiso para acceder al GPS
    private void solicitarPermisoAlUsuario() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MainActivity.REQUEST_CODE_ASK_PERMISSIONS);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        if(REQUEST_CODE_ASK_PERMISSIONS == requestCode) {//compara la llave que tengo al inicio con la que recibo(debe ser igual, solo hay una)
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // OK Do something with..
            } else {
                new PermissionDialogFragment().show(getFragmentManager(), "permissions");//le indico el porque necesito el permiso
                // permission denied, boo! Disable the functionality that depends on this permission.
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        switch (item.getItemId()){
            case R.id.action_refresh:
                //consultarUbicacion();----------------------debe reemplazar a postalCode
                String postalCode = "3685533"; //city?id=3685533
                new FetchWeatherTask().execute(postalCode);
            case R.id.action_settings:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //reemplazo el fragmento actual con el de settings
                fragmentTransaction.replace(R.id.container, new SettingFragment());
                //guarda la instancia del fragmento para volver a ella con el boton back
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

        }

        return true;
    }
/*
    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
        //esta variable se usa para consultas en el Log esta implementada mas abajo, nothing to worry
        private final String LOG_TAG = FragmentMain.FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            //se asegura de que el parametro que recibe(postalcode?city) no este vacio
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch, so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string, its obtained from query
            String forecastJsonStr = null;

            try {

                final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/city?id=";
                final String UNITS = "metric";
                final String CNT = "7";//numero de dias a mostrar
                final String APPID = BuildConfig.openweathermap_API_KEY;

                URL url = new URL(BASE_URL + params[0] + "&units=metric&cnt=3&APPID=dc53a9328f075039ef6bb8946a6e0a1e");
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                Log.v(LOG_TAG, "Forecast JSON String" + buffer.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private void consultarUbicacion() {
            // Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (displayGpsStatus()) {
                MyLocationListener locationListener = new MyLocationListener();
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                } catch (SecurityException e) {
                    new PermissionDialogFragment().show(getFragmentManager(), "permissions");
                }
            } else {
                Log.i("reymar", "Your GPS is: OFF");
            }
        }

        //Method to Check GPS is enable or disable
        private Boolean displayGpsStatus() {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        //Listener class to get coordinates
        private class MyLocationListener implements LocationListener {
            @Override
            public void onLocationChanged(Location loc) {

                Log.i("reymar", "Longitude: " + loc.getLongitude());
                Log.i("reymar", "Latitude: " + loc.getLatitude());

                //to get City-Name from coordinates
                String cityName = null;
                Geocoder gcd = new Geocoder(getApplicationContext(),
                        Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(loc.getLatitude(), loc
                            .getLongitude(), 1);
                    if (addresses.size() > 0)
                        System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("reymar", "My Currrent City is: " + cityName);

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider,
                                        int status, Bundle extras) {
                // TODO Auto-generated method stub
            }
        }
    }
*/
}

/*
    @Override
    public void comunicacion(ArrayList<String> weekForecast) {

        ArrayAdapter<String> forecastAdapter = new ArrayAdapter<>(
                this, // The current context (this activity)
                R.layout.list_item_forecast, // The name of the layout ID.
                R.id.list_item_forecast_textview, // The ID of the textview to populate.
                weekForecast);
        //View view = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) this.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

    }
*/