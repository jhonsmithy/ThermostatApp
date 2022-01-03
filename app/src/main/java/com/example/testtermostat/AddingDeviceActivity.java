package com.example.testtermostat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class AddingDeviceActivity extends AppCompatActivity {

    private LinearLayout addingDevice;
    private LinearLayout addingDeviceSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_device);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("Добавить устройство");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        addingDevice = findViewById(R.id.add_device);
        addingDeviceSettings = findViewById(R.id.add_device_settings);
        addingDevice.setVisibility(View.VISIBLE);
        addingDeviceSettings.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkNetwork(View view) {
        addingDevice.setVisibility(View.GONE);
        addingDeviceSettings.setVisibility(View.VISIBLE);
    }

    public void connect(View view) {
        finish();
    }
}