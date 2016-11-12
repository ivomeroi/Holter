package com.example.anto.holterbluetoothinteligente;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean fp;
    int numsintomas = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TableLayout tabla;
    BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();

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
            }
            else {
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
        android.support.v4.app.Fragment fragment = null;
        String title = "";

        switch(itemnum) {
            case 1:
                fragment = new principal();
                fp = true;
                title = getString(R.string.principal);
                break;
            case 2:
                fragment = new latidos();
                fp = false;
                title = getString(R.string.latidos);
                break;
            case 3:
                fragment = new fc();
                title = getString(R.string.fc);
                fp = false;
                break;
            case 4:
                fragment = new resumen();
                title = getString(R.string.resumen);
                fp = false;
                break;
            case 5:
                fragment = new historialalertas();
                title = getString(R.string.ha);
                fp = false;
                break;
            case 6:
                fragment = new diariosintomas();
                title = getString(R.string.sintomas);
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

    public void pausar(View view) {
        Button boton = (Button) findViewById(R.id.pausar);

        if (boton.getText().equals("Pausar")) {
            stopService(new Intent(MainActivity.this, analisisecg.class));
            Toast.makeText(this, "Se pausó el análisis", Toast.LENGTH_SHORT).show();
            boton.setText("Reanudar");
        }
        else {
            boton.setText("Pausar");

            if (!btadapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
            else {
                startService(new Intent(this, analisisecg.class));
            }
        }
    }

    public void finalizar(View view) {
        Button boton1 = (Button) findViewById(R.id.finalizar);
        Button boton2 = (Button) findViewById(R.id.pausar);

        if (boton1.getText().equals("Finalizar")) {
            stopService(new Intent(MainActivity.this, analisisecg.class));
            Toast.makeText(this, "Se detuvo el análisis", Toast.LENGTH_SHORT).show();
            boton1.setText("Reiniciar");
            boton2.setVisibility(View.GONE);
        }
        else {
            editor.clear();
            editor.putBoolean("mostrarinicio", true);
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), inicio1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
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

    public void agregar (View view) {
        tabla = (TableLayout) findViewById(R.id.tabla4);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Agregar síntoma");
        alertDialog.setMessage("Describa sus síntomas. Ej.: palpitaciones, mareos.");
        final EditText texto = new EditText(MainActivity.this);
        texto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        alertDialog.setView(texto);
        alertDialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
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