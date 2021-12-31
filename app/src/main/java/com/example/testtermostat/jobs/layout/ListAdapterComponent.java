package com.example.testtermostat.jobs.layout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.testtermostat.jobs.ISetNewComponent;
import com.example.testtermostat.jobs.widget.FilterWidget;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterComponent extends ArrayAdapter<String>
{
    private LayoutInflater inflater;
    private int layout;
    private List<String> component = new ArrayList<String>();
    private Context context;
    private int resource;
    private MqttAndroidClient mqttClient;
    private ISetNewComponent isnc;

    public ListAdapterComponent(Context context, int resource, List<String> s, ISetNewComponent isnc) {
        super(context, resource, s);
        component = s;
        this.layout = resource;
        this.isnc = isnc;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(this.layout, parent, false);
        FilterWidget fw = new FilterWidget(view, component.get(position), isnc);
//        Log.d("bag", "view>> "+fw.getView());
        return fw.getView();
    }

    public void addWidget(String s)
    {
//        if (component.size()<1)
        component.add(s);
    }

}
