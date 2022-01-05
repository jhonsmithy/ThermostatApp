package com.example.testtermostat.jobs.devicetype;

import android.widget.ListView;

import com.example.testtermostat.jobs.FilterMQTTMessage;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONException;
import org.json.JSONObject;

public class DeviceType implements IDeviceType
{
    private String serverUri;  // Здесь вы можете ввести доменное имя + номер порта 1883 для различных облачных платформ IoT. Примечание: префикс «tcp: //» обязателен. Я не писал его раньше, поэтому долго не могу подключиться к нему.
    private String userName;                    // Тогда ваше имя пользователя, Alibaba Cloud, Tencent Cloud, Baidu Yuntian Gongwu подключается к этим платформам, оно будет автоматически сгенерировано после создания нового устройства
    private String passWord;                    // Пароль, соответствующий имени пользователя, те же самые различные облачные платформы будут генерировать пароль соответственно, здесь моя платформа EMQ не ограничена, поэтому имя пользователя и пароль могут быть введены случайно
    private String clientId; // clientId очень важен и не может быть повторен, иначе он не будет подключен, поэтому я определил его как приложение + текущее время
    private String channelName;
    private String topicHello;

    @Override
    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    @Override
    public String getServerUri() {
        return serverUri;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String getPassWord() {
        return passWord;
    }

    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public void setTopicHello(String topicHello) {
        this.topicHello = topicHello;
    }

    @Override
    public String getTopicHello() {
        return topicHello;
    }

    @Override
    public JSONObject getJsonObject() {
        JSONObject o = new JSONObject();
        try {
            o.put("serverUri", serverUri);
            o.put("userName", userName);
            o.put("passWord", passWord);
            o.put("clientId", clientId);
            o.put("channelName", channelName);
            o.put("topicHello", topicHello);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public void setJsonObject(JSONObject o) {
        try {
            serverUri = o.getString("serverUri");
            userName = o.getString("userName");
            passWord = o.getString("passWord");
            clientId = o.getString("clientId");
            channelName = o.getString("channelName");
            topicHello = o.getString("topicHello");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
