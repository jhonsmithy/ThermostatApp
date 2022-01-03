package com.example.testtermostat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class AddingDeviceActivity extends AppCompatActivity {

    private LinearLayout addingDevice;
    private LinearLayout addingDeviceSettings;
    private EditText usidText;
    private EditText passwordText;

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
        usidText = findViewById(R.id.login_WiFi);
        passwordText = findViewById(R.id.password_WiFi);
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
        if (itsOnline(this)) {
            addingDevice.setVisibility(View.GONE);
            addingDeviceSettings.setVisibility(View.VISIBLE);
        }
        else
        {
            Toast toast = Toast.makeText(this, "Соединение отсутствует", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 0,160);   // import android.view.Gravity;
            toast.show();
        }
    }

    public void connect(View view) {
        if (setWiFiConnection(usidText.getText().toString(), passwordText.getText().toString())) {
            resetWiFiConnection();
            finish();
        }
        else
        {
            Toast toast = Toast.makeText(this, "Данные не установлены", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 0,160);   // import android.view.Gravity;
            toast.show();
        }
    }

    private boolean setWiFiConnection(String usid, String password)
    {
        try {
            URL url = new URL("http://admin:admin@192.168.4.1/set?routerssid="+usid+"&routerpass="+password);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // все ок
                return true;
            } else {
                // ошибка
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean resetWiFiConnection()
    {
        try {
            URL url = new URL("http://admin:admin@192.168.4.1/set?reset");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // все ок
                return true;
            } else {
                // ошибка
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean itsOnline(Context context) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

            int timeoutMs = 2000;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("192.168.4.1", 80);
//            SocketAddress sockaddr = new InetSocketAddress("192.168.1.107", 80);

            sock.connect(sockaddr, timeoutMs);
            sock.close();
            Log.i("CONNECTION STATUS:", "connected");

            return true;
        } catch (IOException e) {
            Log.i("CONNECTION STATUS:", "disconnected");
            return false;
        }
    }
}