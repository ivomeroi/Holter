package com.example.anto.holterbluetoothinteligente;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean fp;
    int numsintomas = 0;
    private long mMillisUntilFinish;
    CountDownTimer countdowntimer;
    CountDownTimer assist;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TableLayout tabla;
    BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
    private static final String BLUETOOTH_ADDRESS = "00:18:E4:34:C5:45"; // Replace with your HC-05's Bluetooth address
    private static final String BLUETOOTH_URL = "btspp://" + BLUETOOTH_ADDRESS + ":1;authenticate=false;encrypt=false;master=false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        principal fragment = new principal();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        getSupportActionBar().setTitle(getString(R.string.principal));
        fp = true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fp) {
                MainActivity.this.moveTaskToBack(true);
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new principal()).commit();
                fp = true;
                getSupportActionBar().setTitle(getString(R.string.principal));
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.principal);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemnum = item.getOrder();

        openfragment(itemnum);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openfragment(int itemnum) {
        Fragment fragment = null;
        String title = "";

        switch (itemnum) {
            case 1:
                fragment = new principal();
                fp = true;
                title = getString(R.string.principal);
                break;
            case 2:
                fragment = new resumen();
                title = getString(R.string.resumen);
                fp = false;
                break;
            case 7:
                fragment = new ajustes();
                title = getString(R.string.action_settings);
                fp = false;
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportActionBar().setTitle(title);
    }

    public void iniciarPausar(View view) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Button boton = (Button) findViewById(R.id.iniciarPausar);
        TextView timer = (TextView) findViewById(R.id.timer);
        String BLUETOOTH_ADDRESS = "00:18:E4:34:C5:45";
        BluetoothDevice device = btadapter.getRemoteDevice(BLUETOOTH_ADDRESS);
        Method m = device.getClass().getMethod("isConnected", (Class[]) null);
        boolean boo = (boolean) m.invoke(device, (Object[]) null);
        if (boton.getText().equals("Pausar")) {
            countdowntimer.cancel();
            stopService(new Intent(MainActivity.this, analisisecg.class));
            Toast.makeText(this, "Se pausó el análisis", Toast.LENGTH_SHORT).show();
            boton.setText("Reanudar");
        } else if (boton.getText().equals("Conectar")){

            if (!boo) {
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
                boton.setText("Iniciar");
                startService(new Intent(MainActivity.this, analisisecg.class));
            }else{
                boton.setText("Iniciar");

            }
            } else if (boton.getText().equals("Iniciar")){
                boton.setText("Pausar");
                mMillisUntilFinish = 60000;
                countdowntimer = new CountDownTimer(mMillisUntilFinish, 1000) {

                    public void onTick(long millisUntilFinished) {
                        timer.setText("Tiempo Restante: " + new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
                        mMillisUntilFinish = millisUntilFinished;
                    }

                    public void onFinish() {
                        finalizar(view);
                    }
                }.start();
            analisisecg.sendDataToPairedDevice("A", device, MainActivity.this);


        } else if (boton.getText().equals("Reanudar")){
            boton.setText("Pausar");
            countdowntimer = new CountDownTimer(mMillisUntilFinish, 1000) {

                public void onTick(long millisUntilFinished) {
                    timer.setText("Tiempo Restante: " + new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
                    mMillisUntilFinish = millisUntilFinished;
                }

                public void onFinish() {
                    finalizar(view);
                }
            }.start();
            analisisecg.sendDataToPairedDevice("A", device, MainActivity.this);
            startService(new Intent(MainActivity.this, analisisecg.class));


        }
    }

    public void finalizar(View view) {
        Button boton1 = (Button) findViewById(R.id.finalizar);
        Button boton2 = (Button) findViewById(R.id.iniciarPausar);
        TextView timer = (TextView) findViewById(R.id.timer);
        String BLUETOOTH_ADDRESS = "00:18:E4:34:C5:45";
        BluetoothDevice device = btadapter.getRemoteDevice(BLUETOOTH_ADDRESS);
        if (boton1.getText().equals("Finalizar")) {
            assist = new CountDownTimer(1, 1) {

                public void onTick(long millisUntilFinished) {
                    countdowntimer.cancel();
                }

                public void onFinish() {
                    countdowntimer.cancel();
                }
            }.start();
            timer.setText("Listo!");

            stopService(new Intent(MainActivity.this, analisisecg.class));
            Toast.makeText(this, "Se detuvo el análisis", Toast.LENGTH_SHORT).show();
            boton1.setText("Reiniciar");
            boton2.setVisibility(View.GONE);


        } else {
            timer.setText("Presione Iniciar");
            boton1.setText("Finalizar");
            boton2.setVisibility(View.VISIBLE);
            boton2.setText("Conectar");
        }
        analisisecg.sendDataToPairedDevice("Z", device, MainActivity.this);

    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);

        if (requestcode == 1) {
            if (resultcode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth habilitado", Toast.LENGTH_SHORT).show();
                startService(new Intent(this, analisisecg.class));
            }
        }
    }

    public void agregar(View view) {
        tabla = (TableLayout) findViewById(R.id.tabla4);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Agregar síntoma");
        alertDialog.setMessage("Describa sus síntomas. Ej.: palpitaciones, mareos.");
        final EditText texto = new EditText(MainActivity.this);
        texto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        alertDialog.setView(texto);
        alertDialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("sintoma" + String.valueOf(numsintomas), texto.getText().toString());
                        editor.putLong("horasintoma" + String.valueOf(numsintomas), Calendar.getInstance().getTimeInMillis());

                        numsintomas++;
                        editor.putInt("numsintomas", numsintomas);
                        editor.apply();

                        Intent intent = new Intent("sintomas");
                        intent.putExtra("sintoma", texto.getText().toString());
                        getApplicationContext().sendBroadcast(intent);

                        TableRow row = new TableRow(getApplicationContext());
                        TextView t1 = new TextView(getApplicationContext()), t2 = new TextView(getApplicationContext()), t3 = new TextView(getApplicationContext());
                        Calendar calendar = Calendar.getInstance();

                        t1.setText(String.format("%02d-%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1));
                        t2.setText(String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                        t3.setText(texto.getText().toString());

                        t1.setGravity(Gravity.CENTER_HORIZONTAL);
                        t2.setGravity(Gravity.CENTER_HORIZONTAL);
                        t3.setGravity(Gravity.CENTER_HORIZONTAL);

                        t1.setTextColor(R.color.gray);
                        t2.setTextColor(R.color.gray);
                        t3.setTextColor(R.color.gray);

                        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.33f);
                        t1.setLayoutParams(params);
                        t2.setLayoutParams(params);
                        t3.setLayoutParams(params);

                        row.addView(t1);
                        row.addView(t2);
                        row.addView(t3);

                        tabla.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

                        if (numsintomas > 0)
                            tabla.setVisibility(View.VISIBLE);
                    }
                });

        alertDialog.show();
    }
}