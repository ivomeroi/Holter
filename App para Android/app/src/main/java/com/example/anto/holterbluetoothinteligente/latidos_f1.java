package com.example.anto.holterbluetoothinteligente;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class latidos_f1 extends Fragment {
    Handler handler = new Handler();
    SharedPreferences sharedPreferences;
    ArrayList<Map<String, String>> list = new ArrayList<>();
    String[] from = {"title", "value"};
    int[] to = {R.id.title, R.id.value};
    SimpleAdapter adapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.latidos_fragment, container, false);
        listView = (ListView) v.findViewById(R.id.list);

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
            int total, normal, ventricular, supra, marcapasos, p1, p2, p3, p4;

            normal = sharedPreferences.getInt("N", 0);
            ventricular = sharedPreferences.getInt("V", 0);
            supra = sharedPreferences.getInt("S", 0);
            marcapasos = sharedPreferences.getInt("M", 0);
            total = normal + ventricular + supra + marcapasos;

            list.clear();
            list.add(putData("Total", String.valueOf(total)));

            if (total == 0) {
                list.add(putData("Normales", String.valueOf(normal)));
                list.add(putData("Ventriculares", String.valueOf(ventricular)));
                list.add(putData("Supraventriculares", String.valueOf(supra)));
                list.add(putData("Marcapasos", String.valueOf(marcapasos)));
            }
            else {
                p1 = Math.round(((float) normal/total)*100);
                p2 = Math.round(((float) ventricular/total)*100);
                p3 = Math.round(((float) supra/total)*100);
                p4 = Math.round(((float) marcapasos/total)*100);

                list.add(putData("Normales", String.format("%d (%d%%)", normal, p1)));
                list.add(putData("Ventriculares", String.format("%d (%d%%)", ventricular, p2)));
                list.add(putData("Supraventriculares", String.format("%d (%d%%)", supra, p3)));
                list.add(putData("Marcapasos", String.format("%d (%d%%)", marcapasos, p4)));
            }

            if (listView.getAdapter() == null) {
                listView.setAdapter(adapter);
            }
            else {
                adapter.notifyDataSetChanged();
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