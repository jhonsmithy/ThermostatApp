package com.example.testtermostat.jobs.widget;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.view.KeyEventDispatcher;

import com.example.testtermostat.R;
import com.example.testtermostat.jobs.ISetNewComponent;

import org.json.JSONException;
import org.json.JSONObject;

public class WidgetOnOff implements ISetStatusComponent
{
    private Switch switch_on_off;
    private View view;
    public View getView(View view, String s, ISetNewComponent isnc)
    {
        try {
//            ImageView iv = view.findViewById(R.id.image_widget);
//            TextView tv1 = view.findViewById(R.id.name_widget);
//            TextView tv2 = view.findViewById(R.id.status_widget);
//            TextView tv3 = view.findViewById(R.id.after_widget);
//            iv.setVisibility(View.INVISIBLE);
//            tv1.setVisibility(View.VISIBLE);
//            tv2.setVisibility(View.VISIBLE);
//            tv3.setVisibility(View.VISIBLE);
            this.view = view;
            setVisible();
            switch_on_off = view.findViewById(R.id.switch_on_of);
            JSONObject o = new JSONObject(s);
            if (!o.isNull("descr"))
                    switch_on_off.setText(o.getString("descr"));
            if (!o.isNull("topic")) {
//                Log.d("bag","a>>0");
                isnc.setNewComponent(o.getString("topic"), this);
                switch_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        try {
                            if (isChecked == false) {

                                    isnc.message(o.getString("topic"), "{\"status\":\"0\"}");

                            }
                            else
                            {
                                isnc.message(o.getString("topic"), "{\"status\":\"1\"}");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void setStatusComponent(String message) {
        try {
            Log.d("debug","message_on_off>> "+message);
            JSONObject o = new JSONObject(message);
            if (!o.isNull("status"))
                if (o.getString("status").equals("1"))
                    switch_on_off.setChecked(true);
                else
                    switch_on_off.setChecked(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setVisible()
    {
        LinearLayout v = view.findViewById(R.id.id_widget_anydata);
        v.setVisibility(View.GONE);
        LinearLayout v1 =view.findViewById(R.id.id_widget_on_of);
        v1.setVisibility(View.VISIBLE);
        LinearLayout v2 = view.findViewById(R.id.id_widget_select);
        v2.setVisibility(View.GONE);
        LinearLayout v3 = view.findViewById(R.id.id_widget_input);
        v3.setVisibility(View.GONE);
        LinearLayout v4 = view.findViewById(R.id.id_widget_chart);
        v4.setVisibility(View.GONE);
    }
}
