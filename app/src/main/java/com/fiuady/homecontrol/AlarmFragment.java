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

/**
 * Created by Angel on 23/05/2017.
 */

public class AlarmFragment extends Fragment {
    TextView textView;
    private BluetoothSocket btSocket = null;
    private BtBackgroundTask btThread = null;
    private boolean sendMessageFlag = false;

    private User user;
    private UserProfile profile;

    private JSONObject jObj;

    private MainActivity mainActivity;
    private Inventory inventory;
    protected ArrayList<ProfileDevice> profileDevices;
    private Switch globalsw;
    private Switch maindoorsw;
    private Switch pirsw;
    private Switch window1sw;
    private Switch window2sw;
    private Switch window3sw;

    private ProfileDevice pir;
    private ProfileDevice window1;
    private ProfileDevice window2;
    private ProfileDevice window3;
    private ProfileDevice alarm;

    private CheckBox pirchk;
    private CheckBox maindoorchk;
    private CheckBox window1chk;
    private CheckBox window2chk;
    private CheckBox window3chk;

    private View alarmImg;
    private OutputStream btOS;
    private BufferedWriter btbw;
    private ProfileDevice doorMain;
    private ProfileDevice door2;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        user=mainActivity.getCurrentUser();
        profile=mainActivity.getCurrentUserProfile();
        inventory = new Inventory(mainActivity);
        profileDevices = mainActivity.getProfileDevices();
        pir = profileDevices.get(9);
        window1 = profileDevices.get(4);
        window2= profileDevices.get(5);
        window3= profileDevices.get(6);
        doorMain=profileDevices.get(2);
        door2=profileDevices.get(3);
        alarm=profileDevices.get(14);


        btSocket = mainActivity.getConnectedSocket();
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
        return inflater.inflate(R.layout.fragment_alarm,container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        globalsw = (Switch)view.findViewById(R.id.alarm_global_sw);
        pirsw = (Switch)view.findViewById(R.id.alarm_pir_sw);
        window1sw = (Switch)view.findViewById(R.id.alarm_window1_sw);
        window2sw = (Switch)view.findViewById(R.id.alarm_window2_sw);
        window3sw = (Switch)view.findViewById(R.id.alarm_window3_sw);
        maindoorsw =(Switch)view.findViewById(R.id.alarm_door1_sw);
        pirchk = (CheckBox)view.findViewById(R.id.pir_chk);
        window1chk= (CheckBox)view.findViewById(R.id.window1_chk);
        window2chk= (CheckBox)view.findViewById(R.id.window2_chk);
        window3chk= (CheckBox)view.findViewById(R.id.window3_chk);
        maindoorchk=(CheckBox)view.findViewById(R.id.door1_chk);
        textView = (TextView)view.findViewById(R.id.txtets);
        alarmImg = view.findViewById(R.id.alarm_img);
        btThread = new BtBackgroundTask(btSocket);
        btThread.execute();


        globalsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setStatus1(isChecked);
                inventory.saveProfileDevice(alarm);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("alarmstatus1", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        pirsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pir.setStatus1(isChecked);
                inventory.saveProfileDevice(pir);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("pirstatus1", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        window1sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                window1.setStatus1(isChecked);
                inventory.saveProfileDevice(window1);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("window1status1", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        window2sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                window2.setStatus1(isChecked);
                inventory.saveProfileDevice(window2);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("window2status1", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        window3sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                window3.setStatus1(isChecked);
                inventory.saveProfileDevice(window3);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("window3status1", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        maindoorsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doorMain.setStatus1(isChecked);
                inventory.saveProfileDevice(doorMain);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("door1status1", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });

        pirchk.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pir.setStatus1(isChecked);
                inventory.saveProfileDevice(pir);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("pirstatus2", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        window1chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                window1.setStatus1(isChecked);
                inventory.saveProfileDevice(window1);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("window1status2", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        window2chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                window2.setStatus1(isChecked);
                inventory.saveProfileDevice(window2);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("window2status2", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        window3chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                window3.setStatus2(isChecked);
                inventory.saveProfileDevice(window3);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("window3status2", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });
        maindoorchk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doorMain.setStatus2(isChecked);
                inventory.saveProfileDevice(doorMain);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("door1status2", isChecked);
                    sendJSON(jO);
                }catch (JSONException o){}
            }
        });

        globalsw.setChecked(alarm.getStatus1());
        pirsw.setChecked(pir.getStatus1());
        window1sw.setChecked(window1.getStatus1());
        window2sw.setChecked(window2.getStatus1());
        window3sw.setChecked(window3.getStatus1());
        maindoorsw.setChecked(doorMain.getStatus1());
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

                    if (inJson.getBoolean("alarmStatus")) {
                        alarmImg.setBackgroundResource(R.drawable.alarmon);
                    } else {
                        alarmImg.setBackgroundResource(R.drawable.alarmoff);
                    }


        } catch (JSONException e) {
        }
        }
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(btThread!=null)
        {
            btThread.cancel(true);
        }
    }
}
