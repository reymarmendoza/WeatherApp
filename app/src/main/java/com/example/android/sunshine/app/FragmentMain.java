package com.example.android.sunshine.app;

import android.app.Fragment;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Units format: &units=metric, &units=imperial
 * Format: by default returns JSON, or &mode=xml, &mode=html
 * [ JSONArray, { JSONObject
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


        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("3685533");
            return true;
        }
        return super.onOptionsItemSelected(item);
        /*
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
        */

/*
        switch (item.getItemId()){
            case R.id.action_refresh:

                //consultarUbicacion();----------------------debe reemplazar a postalCode

                String postalCode = "3685533";
                new FetchWeatherTask().execute(postalCode);

            case R.id.action_settings:
                //nothing for now
        }
        return true;
*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        List<String> weekForecast = new ArrayList<>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter =
                new ArrayAdapter<>(
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

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        //esta variable se usa para consultas en el Log esta implementada mas abajo, nothing to worry
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            //se asegura de que el parametro que recibe(postalcode?city) no este vacio
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch, so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string, its obtained from query
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query, ESTOS PARAMETROS DEBO PEDIRLOS POR SETTINGS
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/city?";
                final String QUERY_PARAM = "id";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.openweathermap_API_KEY)
                        .build();
                //convierto el uri a la url
                URL url = new URL(uri.toString());
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
                forecastJsonStr = buffer.toString();

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

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null){
                mForecastAdapter.clear();
                for(String dayForecastStr : result){
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "main";
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";
            final String OWM_DESCRIPTION = "description";
            //tengo un string con los datos del query
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            //captura el array LIST que esta dentro del objeto JSON
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            //se crea el formato que se va a usar para mostrar al usuario: Monday, December 26, 2016
            //String day = getReadableDateString();
            String[] resultStrs = new String[numDays];

            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day = getReadableDateString();
                String description;
                String highAndLow;
                // capturo LIST por LIST guardado en el objeto de la consulta al API, el resto de info no la necesito
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // captura el objeto weather
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                // obtiene description del objeto que weatherObject
                description = weatherObject.getString(OWM_DESCRIPTION);

                // obtengo la temperatura
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;

        }

        private String getReadableDateString() {

            return DateFormat.getDateInstance(DateFormat.FULL)
                    .format(Calendar.getInstance().getTimeInMillis());//Monday, December 26, 2016

        }

        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            return roundedHigh + "/" + roundedLow;
        }

    }

}

/*


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

// ----Method to Check GPS is enable or disable -----
private Boolean displayGpsStatus() {
    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
}

// ----------Listener class to get coordinates -------------
private class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location loc) {

        Log.i("reymar", "Longitude: " +loc.getLongitude());
        Log.i("reymar", "Latitude: " +loc.getLatitude());

// ----------to get City-Name from coordinates -------------
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
 */