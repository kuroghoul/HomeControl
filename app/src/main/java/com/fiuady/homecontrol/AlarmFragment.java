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
    private Switch pirsw;
    private Switch window1sw;
    private Switch window2sw;
    private Switch window3sw;

    private ProfileDevice pir;
    private ProfileDevice window1;
    private ProfileDevice window2;
    private ProfileDevice window3;

    private CheckBox pirchk;
    private CheckBox window1chk;
    private CheckBox window2chk;
    private CheckBox window3chk;
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

        btSocket = mainActivity.getConnectedSocket();
        //try {
        //    if(btSocket!=null) {
        //        BufferedReader br = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
//
//
        //        btThread = new BtBackgroundTask(btSocket);
        //        btThread.execute();
        //        Log.d("CREACIÓN DEL BT TASK = ", "true");
        //        //Log.d("BufferReader1 ", br.readLine());
        //    }
        //}catch (IOException e){}
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
        pirchk = (CheckBox)view.findViewById(R.id.pir_chk);
        window1chk= (CheckBox)view.findViewById(R.id.window1_chk);
        window2chk= (CheckBox)view.findViewById(R.id.window2_chk);
        window3chk= (CheckBox)view.findViewById(R.id.window3_chk);
        textView = (TextView)view.findViewById(R.id.txtets);
        btThread = new BtBackgroundTask(btSocket);
        btThread.execute();
    }





    private class BtBackgroundTask extends AsyncTask<BluetoothSocket, String, Void> {
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
//hasoudjhbasioñdbf
        @Override
        protected Void doInBackground(BluetoothSocket... params) {
            try {

                while (!isCancelled()) {
                    String readMessage = br.readLine();
                    if(sendMessageFlag)
                    {
                        bw.flush();
                        bw.write(jObj.toString());
                        bw.flush();
                        sendMessageFlag=false;
                    }
                    publishProgress(readMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            try{
                JSONObject inJson= new JSONObject(values[0]);
                textView.setText(inJson.toString());
                window1chk.setChecked(inJson.getInt("window1")==1);
                window2chk.setChecked(inJson.getInt("window2")==1);
            }
            catch (JSONException o){}


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
