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
            //valida si el permiso GPS NO fue otorgado por la solicitud que se hace en el manifest
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //si el usuario uso la opcion de no volver a preguntar y selecciono no otrgar el permiso al GPS, le digo poruqe es necesario
                solicitarPermisoAlUsuario();


/*
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.i("rootManager","should");
                    //le indica al usuario porque deberia permitir el acceso al GPS(en este caso)
                    new PermissionDialogFragment().show(getFragmentManager(), "permissions");
                    solicitarPermisoAlUsuario();
                }else{//SI el permiso fue otorgado
                    Log.i("rootManager","Se ha otorgado el acceso");
                }
*/
            }
        }

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
    //solicita permiso para acceder al GPS
    private void solicitarPermisoAlUsuario() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_ASK_PERMISSIONS);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        if(REQUEST_CODE_ASK_PERMISSIONS == requestCode){//compara la llave que tengo al inicio con la que recibo(debe ser igual, solo hay una)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//como devuelve un array me posiciono en 0
                // OK Do something with..
                Log.i("rootManager","podemos seguir con el programa");
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new PermissionDialogFragment().show(getFragmentManager(), "permissions");//le indico el porque necesito el permiso
                }
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

