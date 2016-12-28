package com.example.android.sunshine.app.Main;
/*
uso del api:
http://www.survivingwithandroid.com/2013/05/build-weather-app-json-http-android.html
puedo usarlo para location          -------------------------por revisar
http://www.survivingwithandroid.com/2014/04/using-android-location-api-weather-app.html
 */
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Settings.SettingActivity;
import com.example.android.sunshine.app.Settings.SettingFragment;

public class MainActivity extends AppCompatActivity {//implements FeederListViewItems

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;//es una llave que se usa para identificar la peticion del permiso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //se valida si el cliente autorizo los permisos que se pidieron en el manifest
        validacionDePermisosSegunVersion();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
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