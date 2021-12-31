package com.example.testtermostat.jobs.widget;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testtermostat.R;
import com.example.testtermostat.jobs.ISetNewComponent;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Widget_chart  implements ISetStatusComponent
{
//    private TextView textStatus;
    private View view;
    private GraphView graph;

    public View getView(View view, String s, ISetNewComponent isnc)
    {
        try {
            this.view = view;
            setVisible();
//            ImageView imageView = view.findViewById(R.id.widget_anydata_image);
//            TextView textName = view.findViewById(R.id.widget_anydata_name);
//            textStatus = view.findViewById(R.id.widget_anydata_status);
//            TextView textAfter = view.findViewById(R.id.widget_anydata_after);
            graph = (GraphView) view.findViewById(R.id.widget_chart);
            JSONObject o = new JSONObject(s);
//            if (!o.isNull("icon")) {
//                if (o.getString("icon").equals("thermometer")) {
//                    imageView.setImageResource(R.drawable.ic_temperature_icon);
//                }
//                if (o.getString("icon").equals("")) {
//                    imageView.setVisibility(View.GONE);
//                }
//            }
//            else
//            {
//                imageView.setVisibility(View.GONE);
//            }
//            if (!o.isNull("descr"))
//                textName.setText(o.getString("descr"));
//            else
//                textName.setVisibility(View.GONE);
//            if (!o.isNull("after"))
//                textAfter.setText(o.getString("after"));
//            else
//                textAfter.setVisibility(View.GONE);

            if (!o.isNull("topic")) {
                isnc.setNewComponent(o.getString("topic"), this);
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
                JSONArray array = new JSONArray(o.getJSONArray("status"));
                DataPoint[] dp = new DataPoint[array.length()];
                for (int i = 0; i< array.length(); i++) {
                    Date date = new Date(array.getJSONObject(i).getLong("x"));
                    Date tmp = new SimpleDateFormat("HH:mm").parse(date);
                    dp[i] = new DataPoint(date,array.getJSONObject(i).getInt("y1"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
    }

    private void setVisible()
    {
        LinearLayout v = view.findViewById(R.id.id_widget_anydata);
        v.setVisibility(View.GONE);
        LinearLayout v1 =view.findViewById(R.id.id_widget_on_of);
        v1.setVisibility(View.GONE);
        LinearLayout v2 = view.findViewById(R.id.id_widget_select);
        v2.setVisibility(View.GONE);
        LinearLayout v3 = view.findViewById(R.id.id_widget_input);
        v3.setVisibility(View.GONE);
        LinearLayout v4 = view.findViewById(R.id.id_widget_chart);
        v4.setVisibility(View.VISIBLE);
    }
}
