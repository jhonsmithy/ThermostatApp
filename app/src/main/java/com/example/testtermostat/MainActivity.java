package com.example.testtermostat;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.testtermostat.databinding.ActivityMainBinding;
import com.example.testtermostat.jobs.FilterMQTTMessage;
import com.example.testtermostat.jobs.devicetype.DeviceType;
import com.google.android.material.navigation.NavigationView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements ISetMQTTClient {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MqttAndroidClient mqttClient;
    private FilterMQTTMessage filterMQTTMessage;
    private File internalStorageDir;
    private File alice;
    private JSONObject jsonObject;
    private DeviceType dt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dt = null;
        internalStorageDir = getFilesDir();
        alice = new File(internalStorageDir, "alice.csv");

        try {
            FileInputStream fin = openFileInput(alice.getName());
            byte[] bytes = new byte[0];
            bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
//            Log.i("json:", "json text >> "+text);
            JSONObject o = new JSONObject(text);
            dt = new DeviceType();
            dt.setJsonObject(o);
        } catch (IOException | JSONException e) {
//            Log.i("json:", "error text >> ");
            e.printStackTrace();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_device, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
//        setContentView(R.layout.fragment_wifi_module);
//        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
//        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshData(); // your code
//                pullToRefresh.setRefreshing(false);
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void helpButtonClick(MenuItem item) {
        String url = "https://www.google.com";
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    @Override
    public void setMQTTClient(MqttAndroidClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @Override
    public MqttAndroidClient getMQTTClient() {
        return mqttClient;
    }

    @Override
    public void setFilterMQTTMessage(FilterMQTTMessage filterMQTTMessage) {
        this.filterMQTTMessage = filterMQTTMessage;
    }

    @Override
    public FilterMQTTMessage getFilterMQTTMessage() {
        return filterMQTTMessage;
    }

    public static void addDevice(DeviceType dt) {
// Create file output stream


    }

    @Override
    public DeviceType getSelectDevice() {
        return dt;
    }

    public void addWiFi(View view) {
        Intent intent = new Intent(this, AddingDeviceActivity.class);
        startActivity(intent);
    }

    public void addWiFiQRcode(View view) {

    }
}