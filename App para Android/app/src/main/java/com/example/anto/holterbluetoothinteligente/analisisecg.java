package com.example.anto.holterbluetoothinteligente;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.telephony.SmsManager;
import android.view.Display;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//TODO
import java.util.stream.Collectors;

public class analisisecg extends Service
        implements GoogleApiClient.ConnectionCallbacks {

    Handler handler = new Handler();
    BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
    static BluetoothSocket btsocket;
    static InputStream instream;
    static boolean servicioactivo = false;
    ArrayList<Double> señalf2 = new ArrayList<>();
    ArrayList<Integer> qrs = new ArrayList<>(), qrssintomas = new ArrayList<>();
    ArrayList<String> clases = new ArrayList<>(), sintomas = new ArrayList<>(), actividades = new ArrayList<>();
    int sum1 = 0, sum2 = 0, intervalos = 0, latidosant = 1, pausas = 0, latidos = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AlarmManager alarmMgr;
    GoogleApiClient googleApiClient;

    public analisisecg() {
    }

//    BroadcastReceiver buscar = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if ((BluetoothDevice.ACTION_FOUND).equals(action)) {
//                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                Toast.makeText(getApplicationContext(), "Encontro " + device.getName(), Toast.LENGTH_SHORT).show();
//
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                if (device.getName().equals("HC-05")) {
//                    Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_SHORT).show();
//                    encontrado = true;
//                } else {
//                    Toast.makeText(getApplicationContext(), "Conectado a " + device.getName(), Toast.LENGTH_SHORT).show();
//                }
//            } else if ((BluetoothAdapter.ACTION_DISCOVERY_FINISHED).equals(action)) {
//                if (!encontrado) {
//                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return;
//                    }
//
//                }
//            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                Set<BluetoothDevice> pairedDevices = btadapter.getBondedDevices();
//
//                for (BluetoothDevice bt : pairedDevices)
//                    if (bt.getName().equals("HC-05")) {
//                        break;
//                    }
//            }
//        }
//    };


    BroadcastReceiver alarmReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            PendingIntent alarmIntent3;

            alarmIntent3 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("alarma1"), 0);
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60 * 1000, alarmIntent3);



        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        servicioactivo = true;

        registerReceiver(alarmReceiver1, new IntentFilter("alarma1"));

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notificacion = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("BT Holter")
                .setContentText("Detectando y analizando señal de ECG...")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notificacion);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        editor.putBoolean("terminado", false);
        editor.apply();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        String BLUETOOTH_ADDRESS = "00:18:E4:34:C5:45";
        BluetoothDevice device = btadapter.getRemoteDevice(BLUETOOTH_ADDRESS);
        Method m = null;
        try {
            m = device.getClass().getMethod("isConnected", (Class[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        boolean boo = false;
        try {
            boo = (boolean) m.invoke(device, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if(!boo){
            new Thread(new conectar(device)).start();
        }
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        PendingIntent actintent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("activity"), 0);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 0, actintent);
    }

    public void onDestroy() {
        super.onDestroy();
        servicioactivo = false;

//        int latidosactual = latidos, fc;
//        fc = (latidosactual - latidosant);
//        latidosant = latidosactual;
//
//        intervalos++;
//        editor.putInt("intervalos", intervalos);
//        editor.putInt(String.valueOf(intervalos - 1), fc);
//
//        sum1 = sum1 + fc;
//        sum2 = sum2 + fc;
//        //sum1 / intervalos
//        editor.putInt("fcavg", 120);

        if (instream != null) {
            try {
                instream.close();
            } catch (Exception e) {
            }
            instream = null;
        }

        if (btsocket != null) {
            try {
                btsocket.close();
            } catch (Exception e) {
            }
            btsocket = null;
        }

        unregisterReceiver(alarmReceiver1);

        editor.putBoolean("terminado", true);
        editor.putLong("horafin", System.currentTimeMillis());
        editor.apply();
    }

    public class conectar implements Runnable {
        public conectar(BluetoothDevice device) {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

            try {
                if (ActivityCompat.checkSelfPermission(analisisecg.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (btsocket == null)
                    btsocket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                btsocket = null;

            }
        }


        public void run() {
            try {
                if (ActivityCompat.checkSelfPermission(analisisecg.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                btsocket.connect();
            } catch (IOException connectException) {
//                try {
//                    handler.post(() -> Toast.makeText(getApplicationContext(), "Falló la conexión", Toast.LENGTH_SHORT).show());
//
//                    btsocket.close();
//                } catch (IOException closeException) {
//                }

                return;
            }

            handler.post(() -> {
                Toast.makeText(getApplicationContext(), "Dispositivo conectado", Toast.LENGTH_SHORT).show();

                if (sharedPreferences.getBoolean("mostrarinicio", true)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    editor.putBoolean("mostrarinicio", false);
                    editor.apply();
                }
            });
            String BLUETOOTH_ADDRESS = "00:18:E4:34:C5:45";
            BluetoothDevice device = btadapter.getRemoteDevice(BLUETOOTH_ADDRESS);
            Method m = null;
            try {
                m = device.getClass().getMethod("isConnected", (Class[]) null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            boolean boo = false;
            try {
                boo = (boolean) m.invoke(device, (Object[]) null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if(boo){
                new Thread(new recibir()).start();
            }

        }


    }


    class recibir implements Runnable {
        int sinqrs, lectura, datoder, dato, r, i, qrsant, qrsnuevo, qrssintant, qrssintnuevo, datoizq = 0, c1 = 0, c2 = 0, c3 = 0, cpr = 0, numlectura = 0;
        boolean buscarpico = false, buscarqrs = false, pr = false;
        double sum, datomilivoltios, datofiltrado, umbral, derivada, max, prerr, postrr, avgrr, maxderivada = 0;

        ArrayList<Double> ventana1 = new ArrayList<>();
        ArrayList<Double> ventana2 = new ArrayList<>();
        ArrayList<Double> señal = new ArrayList<>(Collections.nCopies(71,0d));
        ArrayList<Double> mf1 = new ArrayList<>(Collections.nCopies(215,0d));
        ArrayList<Double> señalf1 = new ArrayList<>(Collections.nCopies(5,0d));
        ArrayList<Double> señallatido = new ArrayList<>();
        Double[] señallatido2 = new Double[181];
//        int lectura, numlectura = 0;
//        double datomilivoltios, datofiltrado;
//
//        ArrayList<Double> ventana1 = new ArrayList<>();
//        ArrayList<Double> ventana2 = new ArrayList<>();
//        ArrayList<Double> señal = new ArrayList<>(Collections.nCopies(71,0d));
//        ArrayList<Double> mf1 = new ArrayList<>(Collections.nCopies(215,0d));
//        ArrayList<Double> señalf1 = new ArrayList<>(Collections.nCopies(5,0d));
//        ArrayList<Double> señallatido = new ArrayList<>();
//        Double[] señallatido2 = new Double[181];
//        List<Integer> result;

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        public recibir() {
            try {
                instream = btsocket.getInputStream();
            } catch (IOException e) {
                instream = null;
            }
        }

        public void run() {
            while (servicioactivo) {
                //try {

//                    lectura = instream.read();
//
//                    datomilivoltios = (lectura * (5.0 / 1024) - 1.5625) / 1.1;
//
//
//                    if (numlectura < 40) {
//                        numlectura++;
//                        ventana1.add(datomilivoltios);
//                        mf1.add(datomilivoltios);
//                    } else {
//                        numlectura = 0;
//                        ventana2.addAll(ventana1);
//                        // Set the command to launch Python and execute the script
//                        //TODO script
//                        String listString = null;
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                            listString = ventana1.stream().map(Object::toString)
//                                    .collect(Collectors.joining(", "));
//                        }
//                        ventana1.clear();
//                        String scriptPath = "preprocess.py";
//                        String[] command = new String[]{"python", scriptPath, listString};
//
//                        // Create a ProcessBuilder object and set the command
//                        ProcessBuilder pb = new ProcessBuilder(command);
//
//                        // Start the process
//                        Process process = pb.start();
//
//                        // Read the output from the process
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                        String output = reader.readLine();
//
//                        // Convert the output to a list of integers
//                        listString = listString.replace('[', ' ');
//                        listString = listString.replace(']', ' ');
//                        result = null;
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                            result = Arrays.stream(listString.split(","))
//                                    .map(String::trim)
//                                    .map(Integer::parseInt)
//                                    .collect(Collectors.toList());
//                        }
//                        Intent intent2 = new Intent("info");
//                        intent2.putExtra("datofiltrado", listString);
//                        getApplicationContext().sendBroadcast(intent2);
//                        String finalListString = listString;
//                        handler.post(new Runnable() {
//                             public void run() {
//                                 Toast.makeText(getApplicationContext(), "Awa " + result, Toast.LENGTH_SHORT).show();
//                             }
//                        });

                        // Wait for the process to finish
                        //int exitCode = process.waitFor();
                  //  }
                //}

//                try {
//                    lectura = instream.read();
//
//                    datomilivoltios = (lectura * (5.0 / 1024)) * (-10);
//
//                    numlectura++;
//                    c3--;
//
//                    señal.remove(0);
//                    señal.add(datomilivoltios);
//
//                    if (numlectura > 35) {
//                        ventana1.clear();
//                        ventana1.addAll(señal);
//                        Collections.sort(ventana1);
//
//                        mf1.remove(0);
//                        mf1.add(ventana1.get(35));
//                    }
//
//                    if (numlectura > 142) {
//                        ventana2.clear();
//                        ventana2.addAll(mf1);
//                        Collections.sort(ventana2);
//
//                        señalf1.remove(0);
//                        señalf1.add(datomilivoltios - (ventana2.get(107)));
//                    }
//
//                    if (numlectura > 144) {
//                        sum = 0;
//
//                        for (i = 0; i < 5; i++) {
//                            sum = sum + señalf1.get(i);
//                        }
//
//                        datofiltrado = sum / 5;
//
//                        señalf2.add(datofiltrado);
//
//                        Intent intent2 = new Intent("info");
//                        intent2.putExtra("datofiltrado", datofiltrado);
//                        getApplicationContext().sendBroadcast(intent2);
//                    }
//
//                    if (c3 == 0) {
//                        señallatido.clear();
//                        señallatido.addAll(señalf2.subList(señalf2.size() - 182, señalf2.size() - 1));
//                    }
//                }

                try {
                    lectura = instream.read();

                    datomilivoltios = (lectura * (5.0 / 1024)) * (-10);

                    numlectura++;
                    c3--;

                    señal.remove(0);
                    señal.add(datomilivoltios);

                    if (numlectura > 35) {
                        ventana1.clear();
                        ventana1.addAll(señal);
                        Collections.sort(ventana1);

                        mf1.remove(0);
                        mf1.add(ventana1.get(35));
                    }

                    if (numlectura > 142) {
                        ventana2.clear();
                        ventana2.addAll(mf1);
                        Collections.sort(ventana2);

                        señalf1.remove(0);
                        señalf1.add(datomilivoltios - (ventana2.get(107)));
                    }

                    if (numlectura > 144) {
                        sum = 0;

                        for (i = 0; i < 5; i++) {
                            sum = sum + señalf1.get(i);
                        }

                        datofiltrado = sum / 5;

                        señalf2.add(datofiltrado);

                        if (señalf2.size() > 1296000) {
                            señalf2.remove(0);

                            for (i = (qrs.size() - 1); i > (-1); i--) {
                                qrsant = qrs.get(i);
                                qrsnuevo = qrsant - 1;

                                if (qrsnuevo < 0) {
                                    qrs.remove(i);
                                    clases.remove(i);
                                    actividades.remove(i);
                                } else {
                                    qrs.set(i, qrsnuevo);
                                }
                            }

                            for (i = (qrssintomas.size() - 1); i > (-1); i--) {
                                qrssintant = qrssintomas.get(i);
                                qrssintnuevo = qrssintant - 1;

                                if (qrssintnuevo < 0) {
                                    qrssintomas.remove(i);
                                    sintomas.remove(i);
                                } else {
                                    qrssintomas.set(i, qrssintnuevo);
                                }
                            }
                        }

                        Intent intent2 = new Intent("info");
                        intent2.putExtra("datofiltrado", datofiltrado);
                        getApplicationContext().sendBroadcast(intent2);
                    }

                    if ((numlectura < 867) & (numlectura > 146)) {
                        derivada = señalf2.get(señalf2.size() - 1) - señalf2.get(señalf2.size() - 3);

                        if (derivada > maxderivada) {
                            maxderivada = derivada;
                        }

                    } else if (numlectura == 867) {
                        umbral = 0.3 * maxderivada;

                        editor.putLong("horacomienzo", System.currentTimeMillis());

                    } else if (numlectura > 867) {
                        sinqrs++;

                        if (sinqrs == 721) {
                            pausas++;
                            editor.putInt("pausas", pausas);
                            editor.apply();
                        }

                        if (sinqrs == 1081) {
                            long h = System.currentTimeMillis();
                            String evento = "Pausa mayor a 2 s";
                        }

                        if (!pr) {
                            derivada = señalf2.get(señalf2.size() - 1) - señalf2.get(señalf2.size() - 3);

                            if (derivada > umbral) {
                                if (!buscarpico)
                                    buscarpico = true;
                                else {
                                    c1++;

                                    if (c1 == 3) {
                                        c1 = 0;
                                        buscarpico = false;
                                        buscarqrs = true;
                                        pr = true;
                                    }
                                }
                            } else {
                                buscarpico = false;
                                c1 = 0;
                            }
                        } else {
                            cpr++;

                            if (buscarqrs) {
                                c2++;

                                if (c2 > 29) {
                                    c2 = 0;
                                    max = 0;
                                    buscarqrs = false;

                                    for (i = (señalf2.size() - 62); i < (señalf2.size() - 1); i++) {
                                        if (Math.abs(señalf2.get(i)) > max) {
                                            max = Math.abs(señalf2.get(i));
                                            r = i;
                                        }
                                    }

                                    qrs.add(r);
                                    //latidos++;
                                    latidos();

                                    c3 = 91 - (señalf2.size() - r);
                                }
                            }

                            if (cpr > 72) {
                                cpr = 0;
                                pr = false;
                            }
                        }
                    }

                    if (c3 == 0) {
                        señallatido.clear();
                        señallatido.addAll(señalf2.subList(señalf2.size() - 182, señalf2.size() - 1));
                    }
                }


                catch (IOException e) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Se perdió la conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    private void latidos(){
        latidos++;
        int latidosactual = latidos, fc;
        fc = (latidosactual - latidosant);
        latidosant = latidosactual;

        intervalos++;
        editor.putInt("intervalos", intervalos);
        editor.putInt(String.valueOf(intervalos - 1), fc);

        sum1 = sum1 + fc;
        sum2 = sum2 + fc;
        //sum1 / intervalos
        editor.putInt("fcavg", 120);
        editor.commit();

    }

    public static void sendDataToPairedDevice(String message, BluetoothDevice device, Context context) {
        byte[] toSend = message.getBytes(Charset.defaultCharset());
        try {
            UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            OutputStream mmOutStream = btsocket.getOutputStream();
            mmOutStream.write(toSend);
            // Your Data is sent to  BT connected paired device ENJOY.
        } catch (Exception e) {
            Toast.makeText(context, "Fallo en la comunicación", Toast.LENGTH_SHORT).show();
        }
    }
}