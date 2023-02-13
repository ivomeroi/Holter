package com.example.anto.holterbluetoothinteligente;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class principal extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int bpm = 120;
    double datomilivoltios;
    double dato;
    double i = 0;
    double periodo = 0.02761;
    long tiempo = System.currentTimeMillis();
    ArrayList<Double> data_resumen = new ArrayList<>();
    //double periodo = 0;

    XYSeries data = new XYSeries("ECG");
    GraphicalView grafico;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        XYSeries linea = new XYSeries("Baseline");
        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        XYMultipleSeriesRenderer msrenderer = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset multidata = new XYMultipleSeriesDataset();

        getActivity().registerReceiver(recibirinfo, new IntentFilter("info"));


        renderer.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        renderer.setLineWidth(3);
        renderer2.setColor(Color.BLACK);
        renderer2.setLineWidth(3);

        msrenderer.addSeriesRenderer(renderer);
        msrenderer.addSeriesRenderer(renderer2);

        msrenderer.setPanEnabled(false);
        msrenderer.setZoomEnabled(false, false);
        msrenderer.setExternalZoomEnabled(false);
        msrenderer.setXTitle("\n\n\nTiempo [s]");
        msrenderer.setYTitle("Voltaje [mV]");
        msrenderer.setXLabelsColor(Color.BLACK);
        msrenderer.setYLabelsColor(0, Color.BLACK);
        msrenderer.setLabelsColor(Color.BLACK);
        msrenderer.setLabelsTextSize(20);
        msrenderer.setShowGrid(true);
        msrenderer.setGridLineWidth(2);
        msrenderer.setGridColor(Color.GRAY);
        msrenderer.setYLabels(7);
        msrenderer.setXLabels(11);
        msrenderer.setXAxisMin(0);
        msrenderer.setXAxisMax(2);
        msrenderer.setYAxisMin(-1.5);
        msrenderer.setYAxisMax(1.5);
        msrenderer.setApplyBackgroundColor(false);
        msrenderer.setAxisTitleTextSize(20);
        msrenderer.setAxesColor(Color.BLACK);
        msrenderer.setXLabelsAlign(Paint.Align.CENTER);
        msrenderer.setXLabelsPadding(5);
        msrenderer.setYLabelsAlign(Paint.Align.RIGHT);
        msrenderer.setYLabelsPadding(5);
        msrenderer.setMargins(new int[]{20, 70, 100, 20});
        msrenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        msrenderer.setShowLegend(false);

        linea.add(0, 0);
        linea.add(2,0);

        multidata.addSeries(data);
        multidata.addSeries(linea);

        grafico = ChartFactory.getLineChartView(getContext(), multidata, msrenderer);

        View v = inflater.inflate(R.layout.fragment_principal, container, false);
        LinearLayout v2 = (LinearLayout) v.findViewById(R.id.grafico);
        v2.addView(grafico);

        Button boton1 = (Button) v.findViewById(R.id.finalizar);
        Button boton2 = (Button) v.findViewById(R.id.iniciarPausar);

        boton1.setVisibility(View.VISIBLE);
        boton2.setVisibility(View.VISIBLE);


        return v;
    }

    @Override
    public void onDestroyView(){
        getActivity().unregisterReceiver(recibirinfo);
        String str = "";
        for (int i = 0; i < data_resumen.size(); i++) {
            str = str + String.valueOf(data_resumen.get(i)) +",";
        }
        editor.putInt("fcavgs", 120);
        editor.putString("data_resumen", str);
        editor.putInt("data_resumen_size", data_resumen.size());
        editor.commit();
        super.onDestroyView();
    }

     private BroadcastReceiver recibirinfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            datomilivoltios = intent.getDoubleExtra("datofiltrado", 0);
            dato = datomilivoltios-0.5;

            if (i > 2) {
                i = 0;
                data.clear();
            }

            data.add(i, dato);


            data_resumen.add(dato);
            grafico.repaint();

            i = i + periodo;


//            Stream<String> result;
//            String listString = intent.getStringExtra("datofiltrado");
//            listString = listString.replace('[', ' ');
//            listString = listString.replace(']', ' ');
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                result = Arrays.stream(listString.split(","));
//                Handler handler = new Handler();
//                Stream<String> finalResult = result;
//                String finalListString = listString;
//                handler.post(new Runnable() {
//                    public void run() {
//                        Toast.makeText(context.getApplicationContext(), "Awa " + finalListString, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }

//
//            //ArrayList<Integer> datomilivoltios = intent.getIntegerArrayListExtra("datofiltrado");
//            for(Integer dato : result) {
//                if (i > 2) {
//                    i = 0;
//                    data.clear();
//                }
//
//                data.add(i, dato);
//                grafico.repaint();
//
//                i = i + periodo;
//            }

        }
    };
}