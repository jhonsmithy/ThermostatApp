package com.example.testtermostat.jobs.devicetype;

import android.widget.ListView;

import com.example.testtermostat.jobs.FilterMQTTMessage;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public interface IDeviceType
{
    public void setServerUri(String serverUri);

    public String getServerUri();

    public void setUserName(String userName);

    public String getUserName();

    public void setPassWord(String passWord);

    public String getPassWord();

    public void setClientId(String clientId);

    public String getClientId();

    public void setChannelName(String channelName);

    public String getChannelName();

    public void setTopicHello(String topicHello);

    public String getTopicHello();
}
