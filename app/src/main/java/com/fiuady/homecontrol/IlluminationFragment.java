package com.fiuady.homecontrol;

import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.fiuady.homecontrol.db.DIMM;
import com.fiuady.homecontrol.db.Inventory;
import com.fiuady.homecontrol.db.ProfileDevice;
import com.fiuady.homecontrol.db.RGB;
import com.fiuady.homecontrol.db.User;
import com.fiuady.homecontrol.db.UserProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Kuro on 21/05/2017.
 */

public class IlluminationFragment extends Fragment{

    private BluetoothSocket btSocket = null;
    private BtBackgroundTask btThread = null;
    private boolean sendMessageFlag = false;
    JSONObject jObj = null;



    private ProfileDevice dimm1;
    private ProfileDevice dimm2;
    private ProfileDevice rgb1;
    private ProfileDevice rgb2;
    private User user;
    private UserProfile profile;

    private MainActivity mainActivity;
    Inventory inventory;


    private SeekBar dimm1SeekBar;
    private SeekBar dimm2SeekBar;

    private SeekBar rgb1SBR;
    private SeekBar rgb1SBG;
    private SeekBar rgb1SBB;

    private SeekBar rgb2SBR;
    private SeekBar rgb2SBG;
    private SeekBar rgb2SBB;

    private View color1;
    private View color2;

    private Switch dimm1sw;
    private Switch dimm2sw;
    private Switch rgb1sw;
    private Switch rgb2sw;

    private CheckBox dimm1chk;
    private CheckBox dimm2chk;
    private CheckBox rgb1chk;
    private CheckBox rgb2chk;


    private Button saveBtn;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity)getActivity();
        inventory = new Inventory(mainActivity);
        user = mainActivity.getCurrentUser();
        profile = mainActivity.getCurrentUserProfile();
        dimm1 = inventory.getProfileDevice(profile.getId(), 0);
        dimm2 = inventory.getProfileDevice(profile.getId(), 1);
        rgb1 = inventory.getProfileDevice(profile.getId(), 12);
        rgb2 = inventory.getProfileDevice(profile.getId(), 13);

        btSocket = mainActivity.getConnectedSocket();
        if(btSocket!=null) {
            btThread = new BtBackgroundTask(btSocket);
            btThread.execute();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_illumination, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        dimm1SeekBar = (SeekBar)view.findViewById(R.id.illumination_seekbar1);
        dimm1SeekBar.setProgress(dimm1.getPwm1());
        dimm2SeekBar = (SeekBar)view.findViewById(R.id.illumination_seekbar2);
        dimm2SeekBar.setProgress(dimm2.getPwm1());

        color1 = view.findViewById(R.id.illumination_color3);
        rgb1SBR = (SeekBar)view.findViewById(R.id.illumination_R3);
        rgb1SBR.setProgress(rgb1.getPwm1());

        rgb1SBG = (SeekBar)view.findViewById(R.id.illumination_G3);
        rgb1SBG.setProgress(rgb1.getPwm2());

        rgb1SBB = (SeekBar)view.findViewById(R.id.illumination_B3);
        rgb1SBB.setProgress(rgb1.getPwm3());

        color2 = view.findViewById(R.id.illumination_color4);
        rgb2SBR = (SeekBar)view.findViewById(R.id.illumination_R4);
        rgb2SBR.setProgress(rgb2.getPwm1());

        rgb2SBG = (SeekBar)view.findViewById(R.id.illumination_G4);
        rgb2SBG.setProgress(rgb2.getPwm2());

        rgb2SBB = (SeekBar)view.findViewById(R.id.illumination_B4);
        rgb2SBB.setProgress(rgb2.getPwm3());

        color1.setBackgroundColor(Color.argb(255, rgb1SBR.getProgress(), rgb1SBG.getProgress(), rgb1SBB.getProgress()));
        color2.setBackgroundColor(Color.argb(255, rgb2SBR.getProgress(), rgb2SBG.getProgress(), rgb2SBB.getProgress()));

        SeekBar.OnSeekBarChangeListener colorListener1 = new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            color1.setBackgroundColor(Color.argb(255, rgb1SBR.getProgress(), rgb1SBG.getProgress(), rgb1SBB.getProgress()));
        }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

        }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

        }
        };

        SeekBar.OnSeekBarChangeListener colorListener2 = new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                color2.setBackgroundColor(Color.argb(255, rgb2SBR.getProgress(), rgb2SBG.getProgress(), rgb2SBB.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        rgb1SBR.setOnSeekBarChangeListener(colorListener1);
        rgb1SBG.setOnSeekBarChangeListener(colorListener1);
        rgb1SBB.setOnSeekBarChangeListener(colorListener1);

        rgb2SBR.setOnSeekBarChangeListener(colorListener2);
        rgb2SBG.setOnSeekBarChangeListener(colorListener2);
        rgb2SBB.setOnSeekBarChangeListener(colorListener2);

        dimm1sw = (Switch)view.findViewById(R.id.illumination_switch1);
        dimm2sw = (Switch)view.findViewById(R.id.illumination_switch2);
        rgb1sw = (Switch)view.findViewById(R.id.illumination_switch3);
        rgb2sw = (Switch)view.findViewById(R.id.illumination_switch4);

        dimm1sw.setChecked(dimm1.getStatus1());
        dimm2sw.setChecked(dimm2.getStatus1());
        rgb1sw.setChecked(rgb1.getStatus1());
        rgb2sw.setChecked(rgb2.getStatus1());

        dimm1chk = (CheckBox)view.findViewById(R.id.illumination_checkbox1);
        dimm2chk = (CheckBox)view.findViewById(R.id.illumination_checkbox2);
        //rgb1chk = (CheckBox)view.findViewById(R.id.illumination_checkbox3);
        //rgb2chk = (CheckBox)view.findViewById(R.id.illumination_checkbox4);

        dimm1chk.setChecked(dimm1.getStatus2());
        dimm2chk.setChecked(dimm2.getStatus2());



        dimm1sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dimm1.setStatus1(isChecked);
            }
        });
        dimm2sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dimm2.setStatus1(isChecked);
            }
        });
        rgb1sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rgb1.setStatus1(isChecked);
            }
        });
        rgb2sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rgb2.setStatus1(isChecked);
            }
        });


        dimm1chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dimm1.setStatus2(isChecked);
            }
        });
        dimm2chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dimm2.setStatus2(isChecked);
            }
        });
        //rgb1chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //    @Override
        //    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //        rgb1.setStatus2(isChecked);
        //    }
        //});
        //rgb2chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //    @Override
        //    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //        rgb2.setStatus2(isChecked);
        //    }
        //});

        saveBtn = (Button)view.findViewById(R.id.illumination_save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIlluminationChanges();
                sendMessageFlag=true;
                getFragmentManager().popBackStackImmediate();
            }
        });

    }






    public void saveIlluminationChanges()
    {
        dimm1.setPwm1(dimm1SeekBar.getProgress());
        dimm1.setStatus1(dimm1sw.isChecked());
        dimm1.setStatus2(dimm1chk.isChecked());

        dimm2.setPwm1(dimm2SeekBar.getProgress());
        dimm2.setStatus1(dimm2sw.isChecked());
        dimm2.setStatus2(dimm2chk.isChecked());

        rgb1.setPwm1(rgb1SBR.getProgress());
        rgb1.setPwm2(rgb1SBG.getProgress());
        rgb1.setPwm3(rgb1SBB.getProgress());
        rgb1.setStatus1(rgb1sw.isChecked());

        rgb2.setPwm1(rgb2SBR.getProgress());
        rgb2.setPwm2(rgb2SBG.getProgress());
        rgb2.setPwm3(rgb2SBB.getProgress());
        rgb2.setStatus1(rgb2sw.isChecked());

        inventory.saveProfileDevice(dimm1);
        inventory.saveProfileDevice(dimm2);
        inventory.saveProfileDevice(rgb1);
        inventory.saveProfileDevice(rgb2);
        //AQUI PRETENDO ENVIAR LA INFORMACIÓN DE LOS DISPOSITIVOS AL ARDUINO---------------------
        if ((btSocket != null) && (btSocket.isConnected())) {
            jObj = new JSONObject();
            try {
                jObj.put("dimm1.pwm", dimm1.getPwm1());
                jObj.put("dimm1.active", dimm1.getStatus1());
                jObj.put("dimm1.sensor", dimm1.getStatus2());
//
                jObj.put("dimm2.pwm", dimm2.getPwm1());
                jObj.put("dimm2.active", dimm2.getStatus1());
                jObj.put("dimm2.sensor", dimm2.getStatus2());

                jObj.put("lR1.pwm", rgb1.getPwm1());
                jObj.put("lG1.pwm", rgb1.getPwm2());
                jObj.put("lB1.pwm", rgb1.getPwm3());
                jObj.put("RGB1.active", rgb1.getStatus1());


                jObj.put("lR1.pwmR", rgb2.getPwm1());
                jObj.put("lG1.pwmG", rgb2.getPwm2());
                jObj.put("lB1.pwmB", rgb2.getPwm3());
                jObj.put("RGB2.active", rgb2.getStatus1());


                sendMessageFlag = true;


            } catch (JSONException e) {
            }

        }
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

        @Override
        protected Void doInBackground(BluetoothSocket... params) {
            try {
                Log.d("Primera ejecucion BT", "true");
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
