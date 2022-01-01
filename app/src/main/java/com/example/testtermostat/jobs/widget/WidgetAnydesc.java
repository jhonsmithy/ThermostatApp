package com.example.testtermostat.jobs.widget;

import android.graphics.drawable.Icon;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.testtermostat.R;
import com.example.testtermostat.jobs.ISetNewComponent;

import org.json.JSONException;
import org.json.JSONObject;

public class WidgetAnydesc implements ISetStatusComponent
{
    private TextView textStatus;
    private View view;
    public View getView(View view, String s, ISetNewComponent isnc)
    {
        try {
            this.view = view;
            setVisible();
            ImageView imageView = view.findViewById(R.id.widget_anydata_image);
            TextView textName = view.findViewById(R.id.widget_anydata_name);
            textStatus = view.findViewById(R.id.widget_anydata_status);
            TextView textAfter = view.findViewById(R.id.widget_anydata_after);
            JSONObject o = new JSONObject(s);
            if (!o.isNull("icon")) {
                if (o.getString("icon").equals("thermometer")) {
                    imageView.setImageResource(R.drawable.ic_temperature_icon);
                }
                if (o.getString("icon").equals("")) {
                    imageView.setVisibility(View.GONE);
                }
            }
            else
            {
                imageView.setVisibility(View.GONE);
            }
            if (!o.isNull("descr"))
                textName.setText(o.getString("descr"));
            else
                textName.setVisibility(View.GONE);
            if (!o.isNull("after"))
                textAfter.setText(o.getString("after"));
            else
                textAfter.setVisibility(View.GONE);

            if (!o.isNull("topic")) {
                isnc.setNewComponent(o.getString("topic"), this);
                String message = isnc.getMapStatus(o.getString("topic"));
//                Log.d("debug","message_test>> "+message);
                if ( (message != null) && !(message.equals("")))
                {
                    setStatusComponent(message);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void setStatusComponent(String message) {
        try {
//            Log.d("debug","message_anydata>> "+message);
            JSONObject o = new JSONObject(message);
            if (!o.isNull("status"))
                textStatus.setText(o.getString("status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setVisible()
    {
        LinearLayout v = view.findViewById(R.id.id_widget_anydata);
        v.setVisibility(View.VISIBLE);
        LinearLayout v1 =view.findViewById(R.id.id_widget_on_of);
        v1.setVisibility(View.GONE);
        LinearLayout v2 = view.findViewById(R.id.id_widget_select);
        v2.setVisibility(View.GONE);
        LinearLayout v3 = view.findViewById(R.id.id_widget_input);
        v3.setVisibility(View.GONE);
        LinearLayout v4 = view.findViewById(R.id.id_widget_chart);
        v4.setVisibility(View.GONE);
    }
}
