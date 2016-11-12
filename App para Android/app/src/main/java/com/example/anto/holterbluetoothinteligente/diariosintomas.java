package com.example.anto.holterbluetoothinteligente;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;

public class diariosintomas extends Fragment {
    TableLayout tabla;
    SharedPreferences sharedPreferences;
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_diariosintomas, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        tabla = (TableLayout) v.findViewById(R.id.tabla4);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        int sintdisponibles, sintmostrados;
        String sintoma;
        long hora;
        sintdisponibles = sharedPreferences.getInt("numsintomas", 0);
        sintmostrados = tabla.getChildCount() - 2;

        if (sintdisponibles > 0) {
            tabla.setVisibility(View.VISIBLE);

            for (int i = sintmostrados; i < sintdisponibles; i++) {
                hora = sharedPreferences.getLong("horasintoma" + String.valueOf(i), 0);
                sintoma = sharedPreferences.getString("sintoma" + String.valueOf(i), "0");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(hora);

                TableRow row = new TableRow(getActivity());
                TextView t1 = new TextView(getContext()), t2 = new TextView(getContext()), t3 = new TextView(getContext());

                t1.setText(String.format("%02d-%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1));
                t2.setText(String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                t3.setText(sintoma);

                t1.setGravity(Gravity.CENTER_HORIZONTAL);
                t2.setGravity(Gravity.CENTER_HORIZONTAL);
                t3.setGravity(Gravity.CENTER_HORIZONTAL);

                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.33f);
                t1.setLayoutParams(params);
                t2.setLayoutParams(params);
                t3.setLayoutParams(params);

                row.addView(t1);
                row.addView(t2);
                row.addView(t3);

                tabla.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                tabla.invalidate();
            }
        }
    }
}