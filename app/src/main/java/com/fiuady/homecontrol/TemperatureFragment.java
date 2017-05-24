package com.fiuady.homecontrol;

import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.fiuady.homecontrol.db.Inventory;
import com.fiuady.homecontrol.db.ProfileDevice;
import com.fiuady.homecontrol.db.User;
import com.fiuady.homecontrol.db.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import io.apptik.widget.MultiSlider;

/**
 * Created by Angel on 24/05/2017.
 */

public class TemperatureFragment extends Fragment {


    private BluetoothSocket btSocket = null;
    private BtBackgroundTask btThread = null;

    private ProfileDevice vent1;
    private ProfileDevice vent2;
    private User user;
    private UserProfile profile;

    private MainActivity mainActivity;
    Inventory inventory;


    private Switch vent1Status1sw;
    private Switch vent2Status1sw;

    private CheckBox vent1Status2chk;
    private CheckBox vent2Status2chk;

    private TextView vent1mintxt;
    private TextView vent2mintxt;

    private TextView vent1maxtxt;
    private TextView vent2maxtxt;

    private MultiSlider vent1minmax;
    private MultiSlider vent2minmax;

    private TextView temp1valtxt;
    private TextView temp2valtxt;

    ArrayList<JSONObject> jsonObjects;
    private OutputStream btOS;
    private BufferedWriter btbw;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity)getActivity();
        inventory = new Inventory(mainActivity);
        user = mainActivity.getCurrentUser();
        profile = mainActivity.getCurrentUserProfile();
        btSocket = mainActivity.getConnectedSocket();
        vent1 = inventory.getProfileDevice(profile.getId(),10);
        vent2 = inventory.getProfileDevice(profile.getId(),11);
        if(btSocket!=null) {
            btThread = new BtBackgroundTask(btSocket);
            btThread.execute();
            try {

                btOS = btSocket.getOutputStream();
                btbw = new BufferedWriter(new OutputStreamWriter(btOS));

            }catch (IOException e){


            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temperature,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        temp1valtxt = (TextView)view.findViewById(R.id.temp1_value);
        temp2valtxt = (TextView)view.findViewById(R.id.temp2_value);
        vent1mintxt = (TextView)view.findViewById(R.id.vent1_min_txt);
        vent1maxtxt = (TextView)view.findViewById(R.id.vent1_max_txt);
        vent2mintxt = (TextView)view.findViewById(R.id.vent2_min_txt);
        vent2maxtxt = (TextView)view.findViewById(R.id.vent2_max_txt);
        vent1Status1sw = (Switch)view.findViewById(R.id.vent1_status1);
        vent1Status2chk = (CheckBox) view.findViewById(R.id.vent1_status2);
        vent2Status1sw = (Switch)view.findViewById(R.id.vent2_status1);
        vent2Status2chk = (CheckBox) view.findViewById(R.id.vent2_status2);


        vent1minmax = (MultiSlider)view.findViewById(R.id.vent1_minmax);

        vent1minmax.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex==0)
                {
                    vent1mintxt.setText("mínima\ntemperatura\n"+ String.valueOf(value) +"°C");
                    try {
                        JSONObject jO = new JSONObject();
                        jO.put("vent1pwm1", value);
                        sendJSON(jO);

                    }catch (JSONException o){}
                }
                else if(thumbIndex==1)
                {
                    vent1maxtxt.setText("máxima\ntemperatura\n"+ String.valueOf(value) +"°C");
                    try {
                        JSONObject jO = new JSONObject();
                        jO.put("vent1pwm2", value);
                        sendJSON(jO);

                    }catch (JSONException o){}
                }
            }
        });
        vent2minmax = (MultiSlider)view.findViewById(R.id.vent2_minmax);

        vent2minmax.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex==0)
                {
                    vent2mintxt.setText("mínima\ntemperatura\n"+ String.valueOf(value) +"°C");

                    try {
                        JSONObject jO = new JSONObject();
                        jO.put("vent2pwm1", value);
                        sendJSON(jO);

                    }catch (JSONException o){}
                }
                else if(thumbIndex==1)
                {
                    vent2maxtxt.setText("máxima\ntemperatura\n"+ String.valueOf(value) +"°C");
                    try {
                        JSONObject jO = new JSONObject();
                        jO.put("vent2pwm2", value);
                        sendJSON(jO);

                    }catch (JSONException o){}
                }
            }
        });


        vent1Status1sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    vent1.setStatus1(isChecked);
                    inventory.saveProfileDevice(vent1);
                    JSONObject jO = new JSONObject();
                    jO.put("vent1status1", isChecked);
                    sendJSON(jO);

                }catch (JSONException o){}
            }
        });
        vent1Status2chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    vent1.setStatus2(isChecked);
                    inventory.saveProfileDevice(vent1);
                    JSONObject jO = new JSONObject();
                    jO.put("vent1status2", isChecked);
                    sendJSON(jO);

                }catch (JSONException o){}
            }
        });
        vent2Status1sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    vent2.setStatus1(isChecked);
                    inventory.saveProfileDevice(vent2);
                    JSONObject jO = new JSONObject();
                    jO.put("vent2status1", isChecked);
                    sendJSON(jO);

                }catch (JSONException o){}
            }
        });
        vent2Status2chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    vent2.setStatus2(isChecked);
                    inventory.saveProfileDevice(vent2);
                    JSONObject jO = new JSONObject();
                    jO.put("vent2status2", isChecked);
                    sendJSON(jO);

                }catch (JSONException o){}
            }
        });




        vent1Status1sw.setChecked(vent1.getStatus1());
        vent1Status2chk.setChecked(vent1.getStatus2());
        vent2Status1sw.setChecked(vent2.getStatus1());
        vent2Status2chk.setChecked(vent2.getStatus1());
        vent1minmax.setMin(vent1.getPwm1());
        vent1minmax.setMax(vent1.getPwm2());
        vent2minmax.setMin(vent2.getPwm1());
        vent2minmax.setMax(vent2.getPwm2());
    }
    void sendJSON (JSONObject jsonObject)
    {
        if ((btSocket != null) && (btSocket.isConnected())) {
            try {
                btbw.write(jsonObject.toString());
                btbw.flush();
            } catch (IOException o) {
            }
        }
    }







        private class BtBackgroundTask extends AsyncTask<JSONObject, String, Void> {
            private InputStream mmInStream = null;
            private OutputStream mmOutStream = null;
            BufferedReader br;
            BufferedWriter bw;

            public BtBackgroundTask(BluetoothSocket bluetoothSocket) {

                try {
                    this.mmInStream = btSocket.getInputStream();
                    this.mmOutStream = btSocket.getOutputStream();
                    br = new BufferedReader(new InputStreamReader(mmInStream));
                    bw = new BufferedWriter(new OutputStreamWriter(mmOutStream));


                }catch (IOException e){


                }
            }

            @Override
            protected Void doInBackground(JSONObject... params) {
                try {
                    while (!isCancelled()) {
                        String readMessage = br.readLine();
                        //if(sendMessageFlag)
                        //{

                        //for(JSONObject jsonObject : jsonObjects)
                        //{
                        //    mmOutStream.flush();
                        //    mmOutStream.write((jsonObject.toString()+"\r").getBytes());
                        //}
                        //    bw.flush();
                        //    bw.write(((JSONObject)params[0]).toString());

                        //    sendMessageFlag=false;
                        //}
                        publishProgress(readMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                try {
                    JSONObject inJson = new JSONObject(values[0]);
                    temp1valtxt.setText((inJson.getString("tempC1")+" °C"));
                    temp2valtxt.setText((inJson.getString("tempC2")+" °C"));
                }catch(JSONException e){}

            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if(btThread!=null)
            {
                btThread.cancel(true);
            }













        }





}
