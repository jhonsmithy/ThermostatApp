package com.example.testtermostat.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.testtermostat.ISetMQTTClient;
import com.example.testtermostat.MainActivity;
import com.example.testtermostat.R;
import com.example.testtermostat.databinding.FragmentHomeBinding;
import com.example.testtermostat.jobs.FilterMQTTMessage;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private String serverUri = "tcp://iotml.ml:1883";  // Здесь вы можете ввести доменное имя + номер порта 1883 для различных облачных платформ IoT. Примечание: префикс «tcp: //» обязателен. Я не писал его раньше, поэтому долго не могу подключиться к нему.
    private String userName = "tim:tim";                    // Тогда ваше имя пользователя, Alibaba Cloud, Tencent Cloud, Baidu Yuntian Gongwu подключается к этим платформам, оно будет автоматически сгенерировано после создания нового устройства
    private String passWord = "tim";                    // Пароль, соответствующий имени пользователя, те же самые различные облачные платформы будут генерировать пароль соответственно, здесь моя платформа EMQ не ограничена, поэтому имя пользователя и пароль могут быть введены случайно
    private String clientId = "app"+System.currentTimeMillis(); // clientId очень важен и не может быть повторен, иначе он не будет подключен, поэтому я определил его как приложение + текущее время
    private MqttAndroidClient mqtt_client;
    private MqttConnectOptions options;
    private FilterMQTTMessage mqttMessage;
    private ListView listView;
    private String channelName = "/IoTmanager/*/config";
    private ISetMQTTClient transfer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



//        final TextView textView = binding.textHome;
        SwipeRefreshLayout pullToRefresh = binding.pullToRefresh;
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        transfer = (ISetMQTTClient) getActivity();
        if (transfer.getListView() != null) {
            listView = transfer.getListView();
        }
        else
            listView = binding.listView;

        if ( transfer.getFilterMQTTMessage() == null ) {
            mqttMessage = new FilterMQTTMessage(getContext(), R.layout.layout);
            mqttMessage.start();

            Runnable runnable = new Runnable() {
                public void run() {
                    mqtt_init_Connect();
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
            transfer.setFilterMQTTMessage(mqttMessage);
            transfer.setMQTTClient(mqtt_client);
        }
        else
        {
            mqtt_client = transfer.getMQTTClient();
            mqttMessage = transfer.getFilterMQTTMessage();
            mqttMessage.setListView(listView, mqtt_client);
        }

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    public void mqtt_init_Connect()
    {

        try {
            // Создаем экземпляр mqtt_client, первый параметр - это контекст приложения, просто заполните его напрямую, а затем укажите serverUri и clientId, которые мы определили
            mqtt_client = new MqttAndroidClient(getActivity(),serverUri,clientId);
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
//                Snackbar.make(MainActivity.this, "Соединение не установлено", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Toast toast = Toast.makeText(MainActivity.this, "Соединение не установлено", Toast.LENGTH_LONG);
////                toast.setGravity(Gravity.TOP, 0,160);   // import android.view.Gravity;
//                toast.show();
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
                Toast toast = Toast.makeText(getContext(), "Соединение потеряно, переподключение", Toast.LENGTH_LONG);
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
                String m = byteArrayToHexString(message.getPayload());
                Log.d("tag", "TOPIC>>"+topic);
                Log.d("tag", "message>>" + m);
//                Thread.sleep(50);
                if (!m.equals(""))
                    mqttMessage.newMessage(topic, m);


            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}