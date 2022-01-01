package com.example.testtermostat.jobs.widget;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.testtermostat.R;
import com.example.testtermostat.jobs.ISetNewComponent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WidgetSelect  implements ISetStatusComponent
{
    private Button status;
    private View view;
    private String[] array;
    private int index = 0;
    public View getView(View view, String s, ISetNewComponent isnc)
    {
        try {
            this.view = view;
            setVisible();
            TextView textName = view.findViewById(R.id.widget_select_name);
            status = view.findViewById(R.id.widget_select_status);
            JSONObject o = new JSONObject(s);
            if (!o.isNull("descr"))
                textName.setText(o.getString("descr"));
            else
                textName.setVisibility(View.GONE);

            if (!o.isNull("topic")) {
                isnc.setNewComponent(o.getString("topic"), this);
            }
            if (!o.isNull("options")) {
                JSONArray a = o.getJSONArray("options");
                array = new String[a.length()];
                for (int i = 0; i<a.length(); i++)
                    array[i] = a.getString(i);
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index++;
                        if (index>=a.length())
                            index = 0;
                        status.setText(array[index]);
                        try {
                            Integer integer = index;
                            isnc.message(o.getString("topic"), integer.toString());
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
            Log.d("debug","message_anydata>> "+message);
            JSONObject o = new JSONObject(message);
            if (!o.isNull("status"))
            {
                index = o.getInt("status");
                status.setText(array[index]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setVisible()
    {
        LinearLayout v = view.findViewById(R.id.id_widget_anydata);
        v.setVisibility(View.GONE);
        LinearLayout v1 =view.findViewById(R.id.id_widget_on_of);
        v1.setVisibility(View.GONE);
        LinearLayout v2 = view.findViewById(R.id.id_widget_select);
        v2.setVisibility(View.VISIBLE);
        LinearLayout v3 = view.findViewById(R.id.id_widget_input);
        v3.setVisibility(View.GONE);
        LinearLayout v4 = view.findViewById(R.id.id_widget_chart);
        v4.setVisibility(View.GONE);
    }
}
