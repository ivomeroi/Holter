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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class latidos_f3 extends Fragment {
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
            int total, aisladas, duplas, tripletes, corridas, p1, p2, p3, p4;

            total = sharedPreferences.getInt("S", 0);
            aisladas = sharedPreferences.getInt("AS", 0);
            duplas = sharedPreferences.getInt("DS", 0);
            tripletes = sharedPreferences.getInt("TS", 0);
            corridas = sharedPreferences.getInt("CS", 0);

            list.clear();
            list.add(putData("Total", String.valueOf(total)));

            if (total == 0) {
                list.add(putData("Aisladas", String.valueOf(aisladas)));
                list.add(putData("Duplas", String.valueOf(duplas)));
                list.add(putData("Tripletes", String.valueOf(tripletes)));
                list.add(putData("Corridas", String.valueOf(corridas)));
            }
            else {
                p1 = Math.round(((float) aisladas/total)*100);
                p2 = Math.round(((float) (duplas*2)/total)*100);
                p3 = Math.round(((float) (tripletes*3)/total)*100);
                p4 = 100 - p1 - p2 - p3;

                list.add(putData("Aisladas", String.format("%d (%d%%)", aisladas, p1)));
                list.add(putData("Duplas", String.format("%d (%d%%)", duplas, p2)));
                list.add(putData("Tripletes", String.format("%d (%d%%)", tripletes, p3)));
                list.add(putData("Corridas", String.format("%d (%d%%)", corridas, p4)));
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