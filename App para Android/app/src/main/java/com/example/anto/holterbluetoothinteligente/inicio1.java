package com.example.anto.holterbluetoothinteligente;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class inicio1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] permissions = new String[2];
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

            if (i > 0) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }

            setContentView(R.layout.activity_inicio1);
        }
        else {
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
    }
}