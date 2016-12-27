package com.example.android.sunshine.app;
/*
uso del api:
http://www.survivingwithandroid.com/2013/05/build-weather-app-json-http-android.html
puedo usarlo para location          -------------------------por revisar
http://www.survivingwithandroid.com/2014/04/using-android-location-api-weather-app.html
 */
import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {//implements FeederListViewItems

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;//es una llave que se usa para identificar la peticion del permiso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //se valida si el cliente autorizo los permisos que se pidieron en el manifest
        validacionDePermisosSegunVersion();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new FragmentMain()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//asi se reemplaza el if
/*
        if(item.getItemId() == R.id.action_settings){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            //reemplazo el fragmento actual con el de settings
            fragmentTransaction.replace(R.id.container, new SettingFragment());
            //guarda la instancia del fragmento para volver a ella con el boton back
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        return true;
*/

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void validacionDePermisosSegunVersion() {
        //validacion de permisos MARSHMALLOW en adelante
        if(Build.VERSION.SDK_INT  > 22){
            //valida si el permiso GPS NO fue otorgado por la solicitud que se hace en el manifest
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //shouldShowRequestPermissionRationale no lo uso porque parece redundate a checkSelfPermission
                solicitarPermisoAlUsuario();
            }
        }
    }
    //solicita permiso para acceder al GPS
    private void solicitarPermisoAlUsuario() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_ASK_PERMISSIONS);

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







//notas


/*

package com.example.android.sunshine.app;
/*
uso del api:
http://www.survivingwithandroid.com/2013/05/build-weather-app-json-http-android.html
acceso desde el api a cucuta:
http://api.openweathermap.org/data/2.5/forecast/city?id=3685533&units=metric&APPID=dc53a9328f075039ef6bb8946a6e0a1e
puedo usarlo para location          -------------------------por revisar
http://www.survivingwithandroid.com/2014/04/using-android-location-api-weather-app.html

import android.Manifest;
        import android.app.Activity;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.view.Menu;

public class MainActivity extends Activity {//implements FeederListViewItems
    //instancio la clase que maneja los metodos de accion
    //WeatherAplication weatherAplication;
    static final int REQUEST_CODE_ASK_PERMISSIONS = 123;//es una llave que se usa para identificar la peticion del permiso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //se valida si el cliente autorizo los permisos que se pidieron en el manifest
        //weatherAplication.validacionDePermisosSegunVersion();
        validacionDePermisosSegunVersion();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentMain()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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

}


*/