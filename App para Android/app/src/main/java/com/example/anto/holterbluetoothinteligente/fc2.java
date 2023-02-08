package com.example.anto.holterbluetoothinteligente;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Calendar;

public class fc2 extends Fragment {
    Handler handler = new Handler();
    SharedPreferences sharedPreferences;
    TimeSeries data = new TimeSeries("FC");
    GraphicalView grafico;
    LinearLayout v2;
    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        XYMultipleSeriesRenderer msrenderer = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset multidata = new XYMultipleSeriesDataset();

        renderer.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        renderer.setLineWidth(3);

        msrenderer.addSeriesRenderer(renderer);

        msrenderer.setPanEnabled(false);
        msrenderer.setZoomEnabled(false, false);
        msrenderer.setExternalZoomEnabled(false);
        msrenderer.setXTitle("\n\n\n\n\n\nDía y hora");
        msrenderer.setYTitle("Frecuencia Cardíaca [lpm]");
        msrenderer.setShowGrid(true);
        msrenderer.setGridColor(Color.GRAY);
        msrenderer.setXLabelsColor(Color.BLACK);
        msrenderer.setYLabelsColor(0, Color.BLACK);
        msrenderer.setLabelsColor(Color.BLACK);
        msrenderer.setLabelsTextSize(20);
        msrenderer.setYLabels(5);
        msrenderer.setXLabels(5);
        msrenderer.setApplyBackgroundColor(false);
        msrenderer.setAxisTitleTextSize(20);
        msrenderer.setAxesColor(Color.BLACK);
        msrenderer.setXLabelsAlign(Paint.Align.CENTER);
        msrenderer.setYLabelsAlign(Paint.Align.RIGHT);
        msrenderer.setXLabelsPadding(0);
        msrenderer.setYLabelsPadding(5);
        msrenderer.setMargins(new int[]{20, 70, 70, 20});
        msrenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        msrenderer.setShowLegend(false);

        multidata.addSeries(data);

        grafico = ChartFactory.getTimeChartView(getContext(), multidata, msrenderer, "dd-MM\nHH:mm");

        View v = inflater.inflate(R.layout.fragment_fc2, container, false);
        v2 = (LinearLayout) v.findViewById(R.id.grafico);
        v2.addView(grafico);

        textView = (TextView) v.findViewById(R.id.textView);

        return v;
    }

    public void onResume() {
        super.onResume();
        handler.post(actualizargrafico);
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(actualizargrafico);
    }

    private Runnable actualizargrafico = new Runnable() {
        @Override
        public void run() {
            if (sharedPreferences.getInt("intervalos", 0) > 1) {
                textView.setVisibility(View.GONE);
                v2.setVisibility(View.VISIBLE);

                int intervalos = sharedPreferences.getInt("intervalos", 0);
                long horacomienzo = sharedPreferences.getLong("horacomienzo", 0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(horacomienzo);

                data.clear();

                for (int i = 0; i < intervalos; i++) {
                    data.add(calendar.getTime(), sharedPreferences.getInt(String.valueOf(i), 0));
                    calendar.add(Calendar.MINUTE, 1);
                }

                grafico.repaint();

            }
            else {
                textView.setText("Aún no hay información disponible.");
                textView.setVisibility(View.VISIBLE);
            }

            if (!sharedPreferences.getBoolean("terminado", false))
                handler.postDelayed(actualizargrafico, 10 * 1000);
        }
    };
}