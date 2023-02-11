package com.example.anto.holterbluetoothinteligente;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class resumen extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
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

        View v = inflater.inflate(R.layout.fragment_resumen, container, false);
        LinearLayout v2 = (LinearLayout) v.findViewById(R.id.grafico);
        v2.addView(grafico);


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

    @SuppressLint("SetTextI18n")
    public void getBpm(LayoutInflater inflater, ViewGroup container,
                       Bundle savedInstanceState, Context context){

        Calendar calendar = Calendar.getInstance();
        int bpm = sharedPreferences.getInt("bpm", 0);
        View v = inflater.inflate(R.layout.fragment_resumen, container, false);
        TextView diagnostico = (TextView) v.findViewById(R.id.diagnostico);
        TextView mailEnviado = (TextView) v.findViewById(R.id.mailEnviado);
        Button enviarMensaje = (Button) v.findViewById(R.id.enviarMensaje);

        try {
            // Set the command to launch Python and execute the script
            //TODO script

            String listString = sharedPreferences.getString("signal", "[0]");

            String scriptPath = "get_bpm.py";
            String[] command = new String[]{"python", scriptPath, listString};

            // Create a ProcessBuilder object and set the command
            ProcessBuilder pb = new ProcessBuilder(command);

            // Start the process
            Process process = pb.start();

            // Read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.readLine();

            // Convert the output to a list of integers
            bpm = Integer.parseInt(output.trim());

            // Wait for the process to finish
            int exitCode = process.waitFor();
            editor.putInt("bpm", bpm);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (bpm > 100){
            editor.putString("diagnostico","Taquicardia");
            diagnostico.setText("Taquicardia");
        }
        else if (bpm < 60){
            editor.putString("diagnostico","Bradicardia");
            diagnostico.setText("Bradicardia");
        }
        else{
            editor.putString("diagnostico","FC Normal");
            diagnostico.setText("Frecuencia Cardiaca Normal");
        }

        editor.apply();


        if (bpm < 60 || bpm >100){
            diagnostico.setTextColor(this.getResources().getColor(R.color.red));
            mailEnviado.setVisibility(View.VISIBLE);
            enviarMensaje.setVisibility(View.VISIBLE);

            String mail = sharedPreferences.getString("mailmedico", "0");
            String tipo = sharedPreferences.getString("diagnostico", "FC Normal");

            ArrayList<String> attachments = new ArrayList<>();
            attachments.add("/data/user/0/com.example.anto.holterbluetoothinteligente/files/" + "informe" + String.format("%d%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)) + ".json");

            if (!mail.equals("")) {
                BackgroundMail.newBuilder(context.getApplicationContext())
                        .withUsername("holterbluetoothinteligente@gmail.com")
                        .withPassword("holter123")
                        .withMailto(mail)
                        .withSubject("Informe Holter Bluetooth Inteligente")
                        .withBody("Se produjo un evento de: " + tipo + " a las " + String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)) + ". Se adjunta la señal de ECG registrada en la última hora, con la correspondiente clasificación de cada latido.")
                        .withAttachments(attachments)
                        .send();
            }

        } else {
            diagnostico.setTextColor(this.getResources().getColor(R.color.gray));
            mailEnviado.setVisibility(View.GONE);
            enviarMensaje.setVisibility(View.GONE);
        }
    }

    public void enviarMensaje(Context context){
        String nombre = sharedPreferences.getString("nombre", "0");
        String numero = sharedPreferences.getString("numemergencia", "0");

        if (!numero.equals("")) {
            SmsManager manager = SmsManager.getDefault();

            if (ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                String mensaje = "Se detectó una situación de emergencia en el electrocardiograma de " + nombre + ". Contacte a un número de emergencia de inmediato";
                ArrayList<String> partes = manager.divideMessage(mensaje);
                manager.sendMultipartTextMessage(numero, null, partes, null, null);
            }
        }
    }
}
