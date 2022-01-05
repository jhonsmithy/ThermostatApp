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

import com.example.testtermostat.jobs.devicetype.DeviceType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.Charset;

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
//        Thread t = new Thread(new Runnable() { public void run() {
//            // your code goes here...
//            addWiFiTermostat();
//        }});
//        t.start();


    }

    public void connect(View view) {
        if (setWiFiConnection(usidText.getText().toString(), passwordText.getText().toString())) {
            String serverUri = "tcp://130.61.92.192:1883";  // Здесь вы можете ввести доменное имя + номер порта 1883 для различных облачных платформ IoT. Примечание: префикс «tcp: //» обязателен. Я не писал его раньше, поэтому долго не могу подключиться к нему.
            String userName = "tim:tim";                    // Тогда ваше имя пользователя, Alibaba Cloud, Tencent Cloud, Baidu Yuntian Gongwu подключается к этим платформам, оно будет автоматически сгенерировано после создания нового устройства
            String passWord = "tim";                    // Пароль, соответствующий имени пользователя, те же самые различные облачные платформы будут генерировать пароль соответственно, здесь моя платформа EMQ не ограничена, поэтому имя пользователя и пароль могут быть введены случайно
            String clientId = "app"+System.currentTimeMillis(); // clientId очень важен и не может быть повторен, иначе он не будет подключен, поэтому я определил его как приложение + текущее время
            String channelName = "/IoTmanager/*/config";
            String topicHello = "/IoTmanager";
            DeviceType dt = new DeviceType();
            dt.setServerUri(serverUri);
            dt.setUserName(userName);
            dt.setPassWord(passWord);
            dt.setClientId(clientId);
            dt.setChannelName(channelName);
            dt.setTopicHello(topicHello);

            try {
                FileOutputStream fos = openFileOutput("alice.csv", MODE_PRIVATE);
                // Write a line to the file
                fos.write(dt.getJsonObject().toString().getBytes());
                // Close the file output stream
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



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

    private void addWiFiTermostat()
    {
        String url = "http://admin:admin@192.168.1.107/?set.mqtt:28";
        try {
            readJsonFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject readJsonFromUrl(String adr) throws IOException, JSONException {
            URL url = new URL(adr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.getResponseCode();
            Log.i("json:", " url >> "+url.toString());
            InputStreamReader is = new InputStreamReader(url.openStream());
            BufferedReader rd = new BufferedReader(is);
            String jsonText = readAll(rd);
            Log.i("json:", "json url >> "+jsonText);
            JSONObject json = new JSONObject(jsonText);
            is.close();
            return json;
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
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