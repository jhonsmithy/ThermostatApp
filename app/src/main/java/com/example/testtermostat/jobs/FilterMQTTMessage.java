package com.example.testtermostat.jobs;

import android.content.Context;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.ListView;

import com.example.testtermostat.jobs.layout.ListAdapterComponent;
import com.example.testtermostat.jobs.widget.ISetStatusComponent;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class FilterMQTTMessage extends HandlerThread implements ISetNewComponent
{
    private HashMap<String, ISetStatusComponent> map = new HashMap<String, ISetStatusComponent>();
    private HashMap<String, String> mapStatus = new HashMap<String, String>();
    private ArrayList<String> topics = new ArrayList<String>();
    private ListView listView;
    private MqttAndroidClient mqttAndroidClient;
    private ListAdapterComponent wid;
    private int check;
    private Context context;
    private int resource;
    private static final String TAG = "MODEL";

    public FilterMQTTMessage(Context context, int resource)
    {
        super(TAG);
        check = 0;
        this.context = context;
        this.resource = resource;
        ArrayList<String> a = new ArrayList<String>();
        wid = new ListAdapterComponent(context, resource, a, this);
//        listView.setAdapter(wid);
    }

    public void setListView(ListView listView, MqttAndroidClient mqttAndroidClient)
    {
        this.mqttAndroidClient = mqttAndroidClient;
        this.listView = listView;

    }

    public void refresh()
    {
        check = 0;
        if (topics.size()>0) {
            try {
                String[] s = new String[topics.size()];
                for (int i = 0; i < topics.size(); i++)
                    s[i] = topics.get(i);
                mqttAndroidClient.unsubscribe(s);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        wid.clear();
        ArrayList<String> a = new ArrayList<String>();
        wid = new ListAdapterComponent(context, resource, a, this);
        map.clear();
        mapStatus.clear();
    }

    public void newMessage(String topic, String message)
    {
        if (topic.indexOf("config") > 0) {
            if (check == 0)
                addView(message);
        }
        else
            if (topic.indexOf("status") > 0) {
                setStatus(topic, message);
                if (check == 0)
                    check = 1;
                if (check == 1)
                {
                    check = 20;
                    MqttMessage m = new MqttMessage();
                    m.setPayload("HELLO".getBytes());
                    try {
                        mqttAndroidClient.publish("/IoTmanager",m);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    private void addView(String message)
    {
        listView.post(new Runnable(){
        public void run() {
//            Log.d("bag", "wid>> " + wid.getCount());
            if (wid.getCount() < 1) {
                wid.addWidget(message);
                listView.setAdapter(wid);
            } else {
                ListAdapterComponent f = (ListAdapterComponent) listView.getAdapter();
                f.addWidget(message);
            }
        }
        } );
//        if (listView != null)
//            listView.setAdapter(wid);
    }

    private void setStatus(String topic, String message)
    {
        listView.post(new Runnable(){
            @Override
            public void run() {
                if ((map!=null) && (map.size()>0)) {
//                    Log.d("bag", "map>> " + map.get(topic));
                    if (map.get(topic) != null) {
                        map.get(topic).setStatusComponent(message);
                        mapStatus.remove(topic);
                        mapStatus.put(topic, message);
                        Log.d("debug","message_test_topic>> "+mapStatus.get(topic));
                    }
                }
            }
        });
    }

    @Override
    public void setNewComponent(String topic, ISetStatusComponent component) {
        try {
//            Log.d("bag","a>>1");
            mqttAndroidClient.subscribe(topic+"/status",1);
        } catch (MqttException e) {
            e.printStackTrace();
//            Log.d("bag","a>>2");
        }
        map.put(topic+"/status", component);
        Log.d("debug","message_test_topic>> "+mapStatus.get(topic+"/status"));
        topics.add(topic+"/status");
    }

    @Override
    public void message(String topic, String message) {
        MqttMessage m = new MqttMessage();
        m.setPayload(message.getBytes());
        try {
            mqttAndroidClient.publish(topic+"/control",m);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMapStatus(String topic) {
        Log.d("debug","message_topic>> "+topic);
        return mapStatus.get(topic+"/status");
    }

}
