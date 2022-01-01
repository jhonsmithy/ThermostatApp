package com.example.testtermostat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.testtermostat.databinding.ActivityMainBinding;
import com.example.testtermostat.jobs.FilterMQTTMessage;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private String serverUri = "tcp://iotml.ml:1883";  // Здесь вы можете ввести доменное имя + номер порта 1883 для различных облачных платформ IoT. Примечание: префикс «tcp: //» обязателен. Я не писал его раньше, поэтому долго не могу подключиться к нему.
    private String userName = "tim:tim";                    // Тогда ваше имя пользователя, Alibaba Cloud, Tencent Cloud, Baidu Yuntian Gongwu подключается к этим платформам, оно будет автоматически сгенерировано после создания нового устройства
    private String passWord = "tim";                    // Пароль, соответствующий имени пользователя, те же самые различные облачные платформы будут генерировать пароль соответственно, здесь моя платформа EMQ не ограничена, поэтому имя пользователя и пароль могут быть введены случайно
    private String clientId = "app"+System.currentTimeMillis(); // clientId очень важен и не может быть повторен, иначе он не будет подключен, поэтому я определил его как приложение + текущее время
    private String mqtt_sub_topic = "";          // Нужно подписаться на темы
    private String mqtt_pub_topic = "";
    private MqttAndroidClient mqtt_client;
    private MqttConnectOptions options;
    private FilterMQTTMessage mqttMessage;
    private ListView listView;
    private String channelName = "/IoTmanager/*/config";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        listView = findViewById(R.id.listView);
        mqttMessage = new FilterMQTTMessage(MainActivity.this, R.layout.layout);
        mqttMessage.start();

        Runnable runnable = new Runnable() {
            public void run() {
                    mqtt_init_Connect();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


    }

    private void refreshData() {
        if (!mqtt_client.isConnected())
        {
            Runnable runnable = new Runnable() {
                public void run() {
                    mqtt_init_Connect();
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        MqttMessage m = new MqttMessage();
        m.setPayload("HELLO".getBytes());
        try {
//            listView.setAdapter(null);
            mqttMessage.refresh();
            mqtt_client.publish("/IoTmanager",m);
        } catch (MqttException e) {
            e.printStackTrace();
        }
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

    public void mqtt_init_Connect()
    {

        try {
            // Создаем экземпляр mqtt_client, первый параметр - это контекст приложения, просто заполните его напрямую, а затем укажите serverUri и clientId, которые мы определили
            mqtt_client = new MqttAndroidClient(getApplicationContext(),serverUri,clientId);
            // Создание и создание экземпляра объекта параметра соединения MQTT
            options = new MqttConnectOptions();
            // Затем устанавливаем соответствующие параметры
            options.setUserName(userName);                  // Устанавливаем имя пользователя подключения
            options.setPassword(passWord.toCharArray());    // Устанавливаем пароль для подключения
            options.setConnectionTimeout(30);               // Устанавливаем период ожидания в секундах
            options.setKeepAliveInterval(60);               // Устанавливаем сердцебиение, 30 с
            options.setAutomaticReconnect(true);            // Следует ли повторно подключаться
            // Устанавливаем, очищать ли сеанс, значение false означает, что сервер будет хранить запись о подключении клиента, значение true означает подключение с новым идентификатором каждый раз, когда вы подключаетесь к серверу
            options.setCleanSession(true);
            mqtt_client.connect(options);

            int i = 0;
            while ((!mqtt_client.isConnected()) && i<10)
            {
                i++;
                Thread.sleep(1000);
                if (mqtt_client.isConnected()) {
                    mqtt_client.subscribe(channelName, 0);
                    MqttMessage m = new MqttMessage();
                    m.setPayload("HELLO".getBytes());
                    mqtt_client.publish("/IoTmanager", m);
                    mqttMessage.setListView(listView, mqtt_client);
//                    pJSONMessage.setMQTTclient(mqtt_client);
                    mqttSetClient();
                }
            }
            if (!mqtt_client.isConnected())
            {
                Toast toast = Toast.makeText(MainActivity.this, "Соединение не установлено", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 0,160);   // import android.view.Gravity;
                toast.show();
            }




        }catch (Exception e) {
            e.printStackTrace();
        }
    }

//преоброзование байтов в сторку
    private String byteArrayToHexString(final byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }

    private void mqttSetClient()
    {
        mqtt_client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast toast = Toast.makeText(MainActivity.this, "Соединение потеряно, переподключение", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 0,160);   // import android.view.Gravity;
                toast.show();
                Runnable runnable = new Runnable() {
                    public void run() {
                        mqtt_init_Connect();
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("tag", "TOPIC>>"+topic);
                Log.d("tag", "message>>" + byteArrayToHexString(message.getPayload()));
//                Thread.sleep(50);
                            try {
                                mqttMessage.newMessage(topic, byteArrayToHexString(message.getPayload()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }


            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}