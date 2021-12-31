package com.example.testtermostat.jobs;

import android.content.Context;

import com.example.testtermostat.jobs.widget.ISetStatusComponent;

import java.util.HashMap;

public interface ISetNewComponent
{
    public void setNewComponent(String topic, ISetStatusComponent component);

    public void message(String topic, String message);

}
