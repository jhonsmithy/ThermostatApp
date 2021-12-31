package com.example.testtermostat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class WiFiScanActivity extends Activity {

    private ListView listWiFi;
    private Button btn;
    public static final String WIFI = "WiFI";
    private WifiManager wifi;
    private String wifis[];
    private WifiScanReceiver wifiReciever;
    private boolean erorrsGPS = true;
    private Thread thread;
    private List<ScanResult> wifiScanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_scan);

        listWiFi = (ListView) findViewById(R.id.ListWiFi);
        btn = findViewById(R.id.ScanWiFiButton);

        erorrsGPS = enableGeoLacation();

        //открыть окно ошибки о gps, необходимо для возвращения списка сетей
        if (erorrsGPS) {
            openDilogGPS();
        } else {
            initializedWiFi();
        }
    }

    private class WifiScanReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {

            wifiScanList = wifi.getScanResults();
//            Integer i1 = wifiScanList.size();
//            btn.setText(i1.toString());
            wifis = new String[wifiScanList.size()];

            for (int i = 0; i < wifiScanList.size(); i++) {
                wifis[i] = ((wifiScanList.get(i).SSID).toString());
            }
            addListenerListWiFI();
            listWiFi.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, wifis));
        }
    }

    public void ScanWiFi(View view) {
//        if ((thread == null) || (thread.getState() == Thread.State.TERMINATED)) {
        wifis = new String[0];
        listWiFi.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, wifis));
        Runnable runnable = new Runnable() {
            public void run() {
//                        WiFiScanActivity.this.setEditable(false);
                registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        Toast toast = Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT);
                WiFiScanActivity.super.onResume();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                unregisterReceiver(wifiReciever);
                WiFiScanActivity.super.onPause();
//                        WiFiScanActivity.this.setEditable(true);
            }
            // Нельзя!
            // TextView infoTextView =
            //         (TextView) findViewById(R.id.textViewInfo);
            // infoTextView.setText("Сегодня коты перебегали дорогу: " + mCounter++ + " раз");

        };
        thread = new Thread(runnable);
        thread.start();
//            thread.setDaemon(true);
//        }
    }

    public void BackClick(View view) {
        finish();
    }

    private void enableWifi() {

        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);

            Toast toast = Toast.makeText(getApplicationContext(), "Wifi Turned On", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean enableGeoLacation() {

        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean mIsGeoDisabled = !mIsGPSEnabled && !mIsNetworkEnabled;
        return mIsGeoDisabled;
    }

    private void openOnGps() {
        if (erorrsGPS) {
            Toast toast = Toast.makeText(getApplicationContext(), "Необходимо включить GPS", Toast.LENGTH_SHORT);
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    private void openDilogGPS() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        openOnGps();
                        erorrsGPS = false;
                        initializedWiFi();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Для удобной работы проложения необходим GPS. Хотите включить?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();
    }

    private void initializedWiFi() {
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //Проверяем включен ли WiFi, если нет то включаем
        enableWifi();
        wifiReciever = new WifiScanReceiver();
        wifi.startScan();
    }

    private void addListenerListWiFI() {
        listWiFi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // по позиции получаем выбранный элемент
                String selectedItem = listWiFi.getAdapter().getItem(position).toString();
//                btn.setText(selectedItem);
                String networkCapabilities = wifiScanList.get(position).capabilities;
                myConnect(selectedItem, "", networkCapabilities);
            }
        });
    }

    private void setEditable(Boolean b) {
        if (b) {
            btn.setEnabled(true);
            btn.setText("Поиск Wi-Fi сетей");
        } else {
            btn.setEnabled(false);
            btn.setText("Идет поиск сетей, подождите");
        }
    }

    private void myConnect(String networkSSID, String networkPass, String networkCapabilities) {
        Toast.makeText(this, "Connecting to network: " + networkSSID, Toast.LENGTH_SHORT).show();

        //
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + networkSSID + "\"";
        if (networkCapabilities.toUpperCase().contains("WEP")) { // WEP Network.
            Toast.makeText(this, "WEP Network", Toast.LENGTH_SHORT).show();

            wifiConfig.wepKeys[0] = "\"" + networkPass + "\"";
            wifiConfig.wepTxKeyIndex = 0;
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (networkCapabilities.toUpperCase().contains("WPA")) { // WPA Network
            Toast.makeText(this, "WPA Network", Toast.LENGTH_SHORT).show();
            wifiConfig.preSharedKey = "\"" + networkPass + "\"";
        } else { // OPEN Network.
            Toast.makeText(this, "OPEN Network", Toast.LENGTH_SHORT).show();
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        this.wifi.addNetwork(wifiConfig);

//        List<WifiConfiguration> list = WiFiScanActivity.this.wifi.getConfiguredNetworks();
//        for( WifiConfiguration config : list ) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        WifiConfiguration config = (WifiConfiguration) wifi.getConfiguredNetworks();
            if(config.SSID != null && config.SSID.equals("\"" + networkSSID + "\"")) {
                this.wifi.disconnect();
                this. wifi.enableNetwork(config.networkId, true);
                this.wifi.reconnect();
//                break;
            }
//        }
    }

}