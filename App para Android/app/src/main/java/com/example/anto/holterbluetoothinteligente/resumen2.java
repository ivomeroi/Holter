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

public class resumen2 extends Fragment {
    TableLayout tabla;
    TextView textView;
    Handler handler = new Handler();
    SharedPreferences sharedPreferences;
    Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_resumen2, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        tabla = (TableLayout) v.findViewById(R.id.tabla2);
        textView = (TextView) v.findViewById(R.id.textView);

        calendar.setTimeInMillis(sharedPreferences.getLong("horacomienzo", 0));

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
            int horasdisponibles, horasmostradas, fcmax, fcmin;
            float fcavg;
            horasdisponibles = sharedPreferences.getInt("horas", 0);
            horasmostradas = tabla.getChildCount() - 2;

            if (horasdisponibles > 0) {
                textView.setVisibility(View.GONE);
                tabla.setVisibility(View.VISIBLE);

                for (int i = horasmostradas; i < horasdisponibles; i++) {
                    fcavg = sharedPreferences.getFloat("fcavghora" + String.valueOf(i), 0);
                    fcmax = sharedPreferences.getInt("fcmaxhora" + String.valueOf(i), 0);
                    fcmin = sharedPreferences.getInt("fcminhora" + String.valueOf(i), 0);

                    TableRow row = new TableRow(getActivity());
                    TextView t1 = new TextView(getContext()), t2 = new TextView(getContext()), t3 = new TextView(getContext()), t4 = new TextView(getContext()), t5 = new TextView(getContext());
                    t1.setText(String.format("%02d-%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1));

                    if (sharedPreferences.getBoolean("terminado", false) & i == (horasdisponibles - 1)) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.setTimeInMillis(sharedPreferences.getLong("horafin", 0));

                        t2.setText(String.format("%d:%02d - %d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE)));
                    } else
                        t2.setText(String.format("%d:%02d - %d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY) + 1, 0));

                    t3.setText(String.format("%.0f", fcavg));
                    t4.setText(String.valueOf(fcmax));
                    t5.setText(String.valueOf(fcmin));

                    t1.setGravity(Gravity.CENTER_HORIZONTAL);
                    t2.setGravity(Gravity.CENTER_HORIZONTAL);
                    t3.setGravity(Gravity.CENTER_HORIZONTAL);
                    t4.setGravity(Gravity.CENTER_HORIZONTAL);
                    t5.setGravity(Gravity.CENTER_HORIZONTAL);

                    row.addView(t1);
                    row.addView(t2);
                    row.addView(t3);
                    row.addView(t4);
                    row.addView(t5);

                    tabla.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    calendar.set(Calendar.MINUTE, 0);
                }
            }
            else {
                textView.setText("Aún no hay información disponible.");
                textView.setVisibility(View.VISIBLE);
            }


            if (!sharedPreferences.getBoolean("terminado", false))
                handler.postDelayed(actualizartabla, 60 * 1000);
        }
    };
}