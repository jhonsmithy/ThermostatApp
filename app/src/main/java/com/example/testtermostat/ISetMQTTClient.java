package com.example.testtermostat;

import android.widget.ListView;

import com.example.testtermostat.jobs.FilterMQTTMessage;

import org.eclipse.paho.android.service.MqttAndroidClient;

public interface ISetMQTTClient
{
    public void setMQTTClient(MqttAndroidClient mqttClient);

    public MqttAndroidClient getMQTTClient();

    public void setFilterMQTTMessage(FilterMQTTMessage filterMQTTMessage);

    public FilterMQTTMessage getFilterMQTTMessage();
}
