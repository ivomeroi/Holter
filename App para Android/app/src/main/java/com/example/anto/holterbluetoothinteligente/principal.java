package com.example.anto.holterbluetoothinteligente;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class principal extends Fragment {
    double datomilivoltios;
    double dato;
    double i = 0;
    double periodo = 0.002761;
    long tiempo = System.currentTimeMillis();
    //double periodo = 0;

    XYSeries data = new XYSeries("ECG");
    GraphicalView grafico;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

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
        Button boton2 = (Button) v.findViewById(R.id.pausar);

        if (sharedPreferences.getBoolean("terminado", false)) {
            boton1.setVisibility(View.VISIBLE);
            boton1.setText("Reiniciar");
            boton2.setVisibility(View.GONE);
        }
        else {
            boton1.setVisibility(View.VISIBLE);
            boton2.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onDestroyView(){
        getActivity().unregisterReceiver(recibirinfo);
        super.onDestroyView();
    }

    private BroadcastReceiver recibirinfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            datomilivoltios = intent.getDoubleExtra("datofiltrado", 0);
            dato = datomilivoltios;

            if (i > 2) {
                i = 0;
                data.clear();
            }

            data.add(i, dato);
            grafico.repaint();

            i = i + periodo;
        }
    };
}