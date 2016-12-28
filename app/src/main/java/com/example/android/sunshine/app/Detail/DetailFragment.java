package com.example.android.sunshine.app.Detail;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

/**
 * Created by reyma on 27/12/2016.
 */
public class DetailFragment extends Fragment {

    private String mForecastStr;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // se crea el intent tomando el contexto desde donde se llama, en este caso es un fragmento
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            //guarda la informaicon exra que trae el intent
            mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            //la informacion que recibe desde la llamada al intent la muestra en el elemento del fragmento que fue creado
            ((TextView) view.findViewById(R.id.detail_text))
                    .setText(mForecastStr);
        }

        return view;

    }
}
