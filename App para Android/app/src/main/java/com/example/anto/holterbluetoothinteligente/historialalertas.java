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

public class historialalertas extends Fragment {
    TableLayout tabla;
    TextView textView;
    Handler handler = new Handler();
    SharedPreferences sharedPreferences;
    Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historial, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        tabla = (TableLayout) v.findViewById(R.id.tabla3);
        textView = (TextView) v.findViewById(R.id.textView);

        return v;
    }

    public void onResume() {
        super.onResume();
        handler.post(actualizartabla);
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(actualizartabla);
    }

    private Runnable actualizartabla = new Runnable() {
        @Override
        public void run() {
            int alertasdisponibles, alertasmostradas;
            long hora;
            String tipo;

            if (sharedPreferences.getInt("alertas", 0) > 0) {
                textView.setVisibility(View.GONE);
                tabla.setVisibility(View.VISIBLE);

                alertasdisponibles = sharedPreferences.getInt("alertas", 0);
                alertasmostradas = tabla.getChildCount() - 2;

                for (int i = alertasmostradas; i < alertasdisponibles; i++) {
                    hora = sharedPreferences.getLong("alertahora" + String.valueOf(i), 0);
                    tipo = sharedPreferences.getString("alertatipo" + String.valueOf(i), "0");
                    calendar.setTimeInMillis(hora);

                    TableRow row = new TableRow(getActivity());
                    TextView t1 = new TextView(getContext()), t2 = new TextView(getContext()), t3 = new TextView(getContext());

                    t1.setText(String.format("%02d-%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1));
                    t2.setText(String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                    t3.setText(tipo);

                    t1.setGravity(Gravity.CENTER_HORIZONTAL);
                    t2.setGravity(Gravity.CENTER_HORIZONTAL);
                    t3.setGravity(Gravity.CENTER_HORIZONTAL);

                    row.addView(t1);
                    row.addView(t2);
                    row.addView(t3);

                    tabla.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
            else {
                textView.setText("Aún no hay información disponible.");
                textView.setVisibility(View.VISIBLE);
            }


            if (!sharedPreferences.getBoolean("terminado", false))
                handler.postDelayed(actualizartabla, 1000);
        }
    };
}