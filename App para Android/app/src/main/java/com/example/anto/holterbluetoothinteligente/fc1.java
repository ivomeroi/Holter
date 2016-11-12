package com.example.anto.holterbluetoothinteligente;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class fc1 extends Fragment {
    Handler handler = new Handler();
    SharedPreferences sharedPreferences;
    ArrayList<Map<String, String>> list = new ArrayList<>();
    String[] from = {"title", "value"};
    int[] to = {R.id.title, R.id.value};
    SimpleAdapter adapter;
    ListView listView;
    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fc1, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        textView = (TextView) v.findViewById(R.id.textView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        adapter = new SimpleAdapter(getActivity(), list, R.layout.listlayout, from, to);

        return v;
    }

    public void onResume() {
        super.onResume();
        handler.post(actualizarUI);
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(actualizarUI);
    }

    private Runnable actualizarUI = new Runnable() {
        @Override
        public void run() {
            int fcmin, fcmax, bradicardia, pausas, taquicardia;
            long horafcmaxmilis, horafcminmilis;
            float fcavg;
            String horafcmax, horafcmin, lalas1, lalas2, s1, s2, s3;
            Calendar calendar = Calendar.getInstance();

            if (sharedPreferences.getInt("intervalos", 0) > 0) {
                textView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                fcavg = sharedPreferences.getFloat("fcavg", 0);
                fcmax = sharedPreferences.getInt("fcmax", 0);
                horafcmaxmilis = sharedPreferences.getLong("horafcmax", 0);
                fcmin = sharedPreferences.getInt("fcmin", 0);
                horafcminmilis = sharedPreferences.getLong("horafcmin", 0);
                pausas = sharedPreferences.getInt("pausas", 0);
                taquicardia = sharedPreferences.getInt("taquicardia", 0);
                bradicardia = sharedPreferences.getInt("bradicardia", 0);

                calendar.setTimeInMillis(horafcmaxmilis);
                horafcmax = String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                if (calendar.get(Calendar.HOUR_OF_DAY) != 1)
                    lalas1 = "las ";
                else
                    lalas1 = "la ";

                calendar.setTimeInMillis(horafcminmilis);
                horafcmin = String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                if (calendar.get(Calendar.HOUR_OF_DAY) != 1)
                    lalas2 = "las ";
                else
                    lalas2 = "la ";

                if (bradicardia == 1)
                    s1 = " evento";
                else
                    s1 = " eventos";

                if (pausas == 1)
                    s2 = " evento";
                else
                    s2 = " eventos";

                if (taquicardia == 1)
                    s3 = " evento";
                else
                    s3 = " eventos";

                list.clear();

                list.add(putData("Promedio", String.format("%.0f lpm", fcavg)));
                list.add(putData("Máxima", String.valueOf(fcmax) + " lpm a " + lalas1 + horafcmax));
                list.add(putData("Mínima", String.valueOf(fcmin) + " lpm a " + lalas2 + horafcmin));
                list.add(putData("Bradicardia (FC < 60 lpm)", String.valueOf(bradicardia) + s1));
                list.add(putData("Pausas (RR > 2 s)", String.valueOf(pausas) + s2));
                list.add(putData("Taquicardia (FC > 100 lpm)", String.valueOf(taquicardia) + s3));

                if (listView.getAdapter() == null) {
                    listView.setAdapter(adapter);
                }
                else {
                    adapter.notifyDataSetChanged();
                }
            }
            else {
                textView.setText("Aún no hay información disponible.");
                textView.setVisibility(View.VISIBLE);
            }

            if (!sharedPreferences.getBoolean("terminado", false))
                handler.postDelayed(actualizarUI, 1000);
        }
    };

    private HashMap<String, String> putData(String title, String value) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("title", title);
        item.put("value", value);
        return item;
    }
}