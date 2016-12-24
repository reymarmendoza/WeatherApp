package com.example.android.sunshine.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.id;

/**
 * Units format: &units=metric, &units=imperial
 * Format: by default returns JSON, or &mode=xml, &mode=html
 */
public class FragmentMain extends Fragment{

    ArrayAdapter<String> mForecastAdapter;
    //FeederListViewItems feederListViewItems;
    public FragmentMain() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// Add this line in order for this fragment to handle menu events.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_refresh:
                //consultarUbicacion();----------------------debe reemplazar a postalCode
                String postalCode = "3685533"; //city?id=3685533
                new FetchWeatherTask().execute(postalCode);
/*  como esta se agrego como un fragmento me permite gestionar su interaccion desde aqui
            case R.id.action_settings:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //reemplazo el fragmento actual con el de settings
                fragmentTransaction.replace(R.id.container, new SettingFragment());
                //guarda la instancia del fragmento para volver a ella con el boton back
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
*/
        }
        return true;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ArrayList<String> weekForecast = new ArrayList<>();
        weekForecast.add("Mon 6/23 - Sunny - 31/17");
        weekForecast.add("Tue 6/24 - Foggy - 21/8");
        weekForecast.add("Wed 6/25 - Cloudy - 22/17");
        weekForecast.add("Thurs 6/26 - Rainy - 18/11");
        weekForecast.add("Fri 6/27 - Foggy - 21/10");
        weekForecast.add("Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18");
        weekForecast.add("Sun 6/29 - Sunny - 20/7");
        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_forecast, // The name of the layout ID.
                R.id.list_item_forecast_textview, // The ID of the textview to populate.
                weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;

    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
        //esta variable se usa para consultas en el Log esta implementada mas abajo, nothing to worry
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            //se asegura de que el parametro que recibe(postalcode?city) no este vacio
            if(params.length == 0){
                return null;
            }

            // These two need to be declared outside the try/catch, so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string, its obtained from query
            String forecastJsonStr = null;

            try{

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
            }catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }finally{
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
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

            /*----Method to Check GPS is enable or disable ----- */
            private Boolean displayGpsStatus() {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }

        /*----------Listener class to get coordinates ------------- */
        private class MyLocationListener implements LocationListener {
            @Override
            public void onLocationChanged(Location loc) {

                Log.i("reymar", "Longitude: " +loc.getLongitude());
                Log.i("reymar", "Latitude: " +loc.getLatitude());

    /*----------to get City-Name from coordinates ------------- */
                String cityName = null;
                Geocoder gcd = new Geocoder(getActivity(),
                        Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(loc.getLatitude(), loc
                            .getLongitude(), 1);
                    if (addresses.size() > 0)
                        System.out.println(addresses.get(0).getLocality());
                    cityName=addresses.get(0).getLocality();
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

            /*
            // Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    //makeUseOfNewLocation(location.getAccuracy());
                    Log.i("reymar", Float.toString(location.getAccuracy()));
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            try{
                // Register the listener with the Location Manager to receive location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }catch (SecurityException e){
                new PermissionDialogFragment().show(getFragmentManager(), "permissions");
            }

/*
    private void consultaAPI() {
        // We create our JSONObject from the data
        JSONObject jObj = new JSONObject(data);

        // We start extracting the info
        Location loc = new Location();

        JSONObject coordObj = getObject("coord", jObj);
        loc.setLatitude(getFloat("lat", coordObj));
        loc.setLongitude(getFloat("lon", coordObj));

        JSONObject sysObj = getObject("sys", jObj);
        loc.setCountry(getString("country", sysObj));
        loc.setSunrise(getInt("sunrise", sysObj));
        loc.setSunset(getInt("sunset", sysObj));
        loc.setCity(getString("name", jObj));
        weather.location = loc;

    }
*/
}