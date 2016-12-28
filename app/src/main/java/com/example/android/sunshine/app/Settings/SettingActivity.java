package com.example.android.sunshine.app.Settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.sunshine.app.R;

/**
 * Created by reyma on 27/12/2016.
 */

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.container, new SettingFragment()).commit();
        }

    }

}
