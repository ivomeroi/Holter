package com.example.anto.holterbluetoothinteligente;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class inicio2 extends AppCompatActivity {
    BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        setContentView(R.layout.activity_inicio2);
    }

    public void comenzar(View view) {
        if (!btadapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        else {
            startService(new Intent(this, analisisecg.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
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
}