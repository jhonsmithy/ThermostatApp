package com.example.testtermostat.jobs.widget;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.testtermostat.jobs.ISetNewComponent;

import org.json.JSONException;
import org.json.JSONObject;

public class FilterWidget {
    private View view;



    public FilterWidget(View view, String s, ISetNewComponent isnc) {
        try {
            this.view = view;
//            Log.d("bag", "s>> "+s);
            JSONObject o =new JSONObject(s);
            if (!o.isNull("widget")) {
                if (!o.getString("widget").equals("")) {
                    if (o.getString("widget").equals("toggle")) {
                        WidgetOnOff wof = new WidgetOnOff();
                        this.view = wof.getView(view, s, isnc);
//                        Log.d("bag", "view1>> " + this.view);
                    } else
                    if (o.getString("widget").equals("anydata"))
                    {
                            WidgetAnydesc wa = new WidgetAnydesc();
                            this.view = wa.getView(view, s, isnc);
                    }
                    if (o.getString("widget").equals("select"))
                    {
                        WidgetSelect ws = new WidgetSelect();
                        this.view = ws.getView(view, s, isnc);
                    }
                    if (o.getString("widget").equals("input"))
                    {
                        WidgetInput wi = new WidgetInput();
                        this.view = wi.getView(view, s, isnc);
                    }
                    if (o.getString("widget").equals("chart"))
                    {
                        Widget_chart wc = new Widget_chart();
                        this.view = wc.getView(view, s, isnc);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.d("bag", "error>> ");
        }
    }

    public View getView() {
//        Log.d("bag", "view2>> "+view);
            return view;
    }
}
