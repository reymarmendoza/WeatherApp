package com.example.android.sunshine.app;

import android.app.Fragment;
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

/**
 * API NAME WeatherApp
 * API KEY dc53a9328f075039ef6bb8946a6e0a1e
 *
 * api.openweathermap.org/data/2.5/forecast/city?id=3685533  //este es el id de cucuta
 *
 * Units format
 * metric api.openweathermap.org/data/2.5/find?q=London&units=metric
 * imperial api.openweathermap.org/data/2.5/find?q=London&units=imperial
 *
 * Format
 * JSON api.openweathermap.org/data/2.5/weather?q=London
 * XML api.openweathermap.org/data/2.5/weather?q=London&mode=xml
 * HTML api.openweathermap.org/data/2.5/weather?q=London&mode=html
 *
 * api.openweathermap.org/data/2.5/forecast/city?id=524901&units=metric
 */
public class FragmentMain extends Fragment {

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
        return item.getItemId() == R.id.action_refresh || super.onOptionsItemSelected(item);
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

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            // These two need to be declared outside the try/catch, so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string, its obtained from query
            String forecastJsonStr = null;

            try{
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/city?id=3685533&units=metric&APPID=dc53a9328f075039ef6bb8946a6e0a1e");
                // Create the request to OpenWeatherMap, and open the connection
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
    }

}