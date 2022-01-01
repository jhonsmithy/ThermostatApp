package com.example.testtermostat.jobs.widget;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.icu.util.Calendar;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.PointerIcon;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.testtermostat.R;
import com.example.testtermostat.jobs.ISetNewComponent;

import org.json.JSONException;
import org.json.JSONObject;

public class WidgetInput  implements ISetStatusComponent
{
    private Button textStatus;
    private View view;
    public View getView(View view, String s, ISetNewComponent isnc)
    {
        try {
            this.view = view;
            setVisible();
            TextView textName = view.findViewById(R.id.widget_input_name);
            textStatus = view.findViewById(R.id.widget_input_status);
            JSONObject o = new JSONObject(s);
            if (!o.isNull("icon")) {
                if (o.getString("icon").equals("thermometer")) {
                    setThermometerStatus(isnc, o);


                } else if (o.getString("icon").equals("alarm-outline")) {
                    setTimeStatus(isnc, o);



                } else if (o.getString("icon").equals("")) {
                    textStatus.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null);
//                    textStatus.setText("test");
                }
                else
                {
                    textStatus.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null);
//                    textStatus.setText("test");
                }
            }
            else
            {
                textStatus.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null);
            }
            
            
            if (!o.isNull("color")) {
                if (o.getString("color").equals("orange"))
                    textStatus.setBackgroundColor(Color.rgb(255, 165, 0));
                else if (o.getString("color").equals("green"))
                    textStatus.setBackgroundColor(Color.GREEN);
                else if (o.getString("color").equals("red"))
                    textStatus.setBackgroundColor(Color.RED);
            }
            
            
            if (!o.isNull("descr"))
                textName.setText(o.getString("descr"));
            else
                textName.setVisibility(View.GONE);

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
            if (!o.isNull("status")) {
                textStatus.setText(o.getString("status"));
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
        v3.setVisibility(View.VISIBLE);
        LinearLayout v4 = view.findViewById(R.id.id_widget_chart);
        v4.setVisibility(View.GONE);
    }

    private void setTimeStatus(ISetNewComponent isnc, JSONObject o)
    {
        textStatus.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(view.getContext(),R.drawable.ic_alarm_outline_icon), null,null, null);
//                    textStatus.setPointerIcon(PointerIcon.getSystemIcon(view.getContext(), R.drawable.ic_temperature_icon));
//                    textStatus.setText("test");

        textStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar dateAndTime=Calendar.getInstance();
                TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateAndTime.set(Calendar.MINUTE, minute);
//                                    setInitialDateTime();
                        if (dateAndTime.getTime().getHours()>9)
                            if (dateAndTime.getTime().getMinutes()>9)
                                textStatus.setText(dateAndTime.getTime().getHours()+":"+dateAndTime.getTime().getMinutes());
                            else
                                textStatus.setText(dateAndTime.getTime().getHours()+":0"+dateAndTime.getTime().getMinutes());
                        else
                        if (dateAndTime.getTime().getMinutes()>9)
                            textStatus.setText("0"+dateAndTime.getTime().getHours()+":"+dateAndTime.getTime().getMinutes());
                        else
                            textStatus.setText("0"+dateAndTime.getTime().getHours()+":0"+dateAndTime.getTime().getMinutes());
                        try {
                            isnc.message(o.getString("topic"), textStatus.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                String s = textStatus.getText().toString();
                if (s.length()==5) {
                    int hour = Integer.parseInt(s.substring(0, 2));
                    int minets = Integer.parseInt(s.substring(3));
                    new TimePickerDialog(view.getContext(), t, hour, minets, true).show();
                }
                else
                {
                    new TimePickerDialog(view.getContext(), t, 0, 0, true).show();
                }
            }
        });
    }

    private void setThermometerStatus(ISetNewComponent isnc, JSONObject o) {
        textStatus.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(view.getContext(),R.drawable.ic_temperature_icon), null,null, null);
//                    textStatus.setPointerIcon(PointerIcon.getSystemIcon(view.getContext(), R.drawable.ic_temperature_icon));
//                    textStatus.setText("test");
        textStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(view.getContext());
//                Log.d("debug","test>> "+mDialogBuilder.getContext());
                final EditText input = new EditText(view.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                mDialogBuilder.setView(input);
                //Настраиваем сообщение в диалоговом окне:
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //Вводим текст и отображаем в строке ввода на основном экране:
                                        textStatus.setText(input.getText());
                                        try {
                                            isnc.message(o.getString("topic"), textStatus.getText().toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                //Создаем AlertDialog:
                AlertDialog alertDialog = mDialogBuilder.create();

                //и отображаем его:
                alertDialog.show();
            }
        });
    }
}
