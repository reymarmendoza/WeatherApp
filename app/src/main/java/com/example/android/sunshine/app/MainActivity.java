package com.example.android.sunshine.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {//implements FeederListViewItems

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;//es una llave que se usa para identificar la peticion del permiso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //validacion de permisos MARSHMALLOW en adelante
        if(Build.VERSION.SDK_INT  > 22){
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;//es una llave que se usa para identificar la peticion del permiso
            //valida si el permiso GPS fue otorgado
            //if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //le indica al usuario porque deberia permitir el acceso al GPS(en este caso)
                new PermissionDialogFragment().show(getFragmentManager(), "permissions");
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


                Log.i("pruebas: ", String.valueOf(getPackageManager().checkPermission(Manifest.permission.INTERNET, getApplicationContext().getPackageName())));
            }else{
                Log.i("pruebas: ", "los permisos necesarios no fueron otorgados");
            }



/*pruebas arriba*/
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
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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

