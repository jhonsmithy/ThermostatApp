package com.example.testtermostat.jobs.widget;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testtermostat.R;
import com.example.testtermostat.jobs.ISetNewComponent;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Widget_chart  implements ISetStatusComponent
{
//    private TextView textStatus;
    private View view;
    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private int size;
    private ArrayList<String> hm = new ArrayList<String>();

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
            graph.setVisibility(View.VISIBLE);

            if (!o.isNull("topic")) {
                isnc.setNewComponent(o.getString("topic"), this);
                String message = isnc.getMapStatus(o.getString("topic"));
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
            {

                JSONArray array = new JSONArray(o.getString("status"));
                if ((array.length()>1) || (series == null)) {
                    graph.getSeries().clear();
                    Log.d("debug","test 1");
                    DataPoint[] dp = new DataPoint[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        Date date = new Date(array.getJSONObject(i).getLong("x"));
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        hm.add(sdf.format(date));
//                    Date tmp = new SimpleDateFormat("HH:mm").parse(sdf.format(date));
                        dp[i] = new DataPoint(i, array.getJSONObject(i).getInt("y1"));
//                    Log.d("debug","date>> "+date);
//                    Log.d("debug","dp>> "+dp[i]);
                    }
                    size = dp.length;
                    series = new LineGraphSeries<DataPoint>(dp);
                    series.setColor(Color.GREEN);
                    graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX) {
                                // show normal x values
//                            Log.d("debug","value>> "+value);
                                int a = (int) value;
                                if ((a >= 0) && (a < size))
                                    return hm.get(a);
                                else if (a==size)
                                    return hm.get(a-1);
                                else
                                    return "";
                            } else {
                                // show currency for y values
                                return super.formatLabel(value, isValueX);
                            }
                        }
                    });
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(dp.length);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScrollable(true);
                    graph.addSeries(series);
                }
                else
                {
                    Log.d("debug","test 2");
                    Date date = new Date(array.getJSONObject(0).getLong("x"));
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    hm.add(sdf.format(date));
                    series.appendData(new DataPoint(size, array.getJSONObject(0).getInt("y1")), true, size+1);
                    size++;
                }
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
        v2.setVisibility(View.GONE);
        LinearLayout v3 = view.findViewById(R.id.id_widget_input);
        v3.setVisibility(View.GONE);
        LinearLayout v4 = view.findViewById(R.id.id_widget_chart);
        v4.setVisibility(View.VISIBLE);
    }
}
