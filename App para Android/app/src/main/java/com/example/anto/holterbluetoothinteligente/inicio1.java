package com.example.anto.holterbluetoothinteligente;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class inicio1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] permissions = new String[4];
        int i = 0;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("mostrarinicio", true)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions[i] = Manifest.permission.ACCESS_COARSE_LOCATION;
                i++;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                permissions[i] = Manifest.permission.SEND_SMS;
                i++;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissions[i] = Manifest.permission.BLUETOOTH_CONNECT;
                i++;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissions[i] = Manifest.permission.BLUETOOTH_SCAN;
                i++;
            }

            if (i > 0) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }

            setContentView(R.layout.activity_inicio1);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    public void continuar(View view) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        editor.putString("nombre", ((EditText) findViewById(R.id.nombre)).getText().toString());
        editor.putString("numemergencia", ((EditText) findViewById(R.id.numero)).getText().toString());
        editor.putString("mailmedico", ((EditText) findViewById(R.id.mail)).getText().toString());

        editor.apply();

        Intent intent = new Intent(this, inicio2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        List<String> a = new ArrayList<String>();
        List<String> u = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices){
            s.add(bt.getName());
            a.add(bt.getAddress());
            u.add((bt.getUuids())[0].getUuid().toString());
        }
        Toast.makeText(this, "A " + s, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "B " + a, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "U " + u, Toast.LENGTH_SHORT).show();

    }
}