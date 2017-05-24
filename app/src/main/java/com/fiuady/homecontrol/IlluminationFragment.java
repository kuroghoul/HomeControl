package com.fiuady.homecontrol;

import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.homecontrol.db.DIMM;
import com.fiuady.homecontrol.db.Inventory;
import com.fiuady.homecontrol.db.ProfileDevice;
import com.fiuady.homecontrol.db.RGB;
import com.fiuady.homecontrol.db.User;
import com.fiuady.homecontrol.db.UserProfile;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Kuro on 21/05/2017.
 */

public class IlluminationFragment extends Fragment{

    private BluetoothSocket btSocket = null;
    //private BtBackgroundTask btThread = null;
    private boolean sendMessageFlag = false;
    JSONObject jObj = null;

    private String commandString;

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

    ArrayList<JSONObject> jsonObjects;
    private OutputStream btOS;
    private BufferedWriter btbw;
    TextView textprobando;

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

        jsonObjects = new ArrayList<>();

        btSocket = mainActivity.getConnectedSocket();
        if(btSocket!=null) {
            //btThread = new BtBackgroundTask(btSocket);
            //btThread.execute();
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
        return inflater.inflate(R.layout.item_illumination, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textprobando = (TextView)view.findViewById(R.id.illumination_description4);
        dimm1SeekBar = (SeekBar)view.findViewById(R.id.illumination_seekbar1);
        dimm1SeekBar.setProgress(dimm1.getPwm1());
        dimm2SeekBar = (SeekBar)view.findViewById(R.id.illumination_seekbar2);
        dimm2SeekBar.setProgress(dimm2.getPwm1());

        dimm1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dimm1.setPwm1(progress);
                inventory.saveProfileDevice(dimm1);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("dimm1.pwm", progress);
                    sendJSON(jO);

                }catch (JSONException o){}


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dimm2SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dimm2.setPwm1(progress);
                inventory.saveProfileDevice(dimm2);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("dimm2.pwm", progress);
                    sendJSON(jO);

                }catch (JSONException o){}


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        color1 = view.findViewById(R.id.illumination_color3);
        color2 = view.findViewById(R.id.illumination_color4);

        color1.setBackgroundColor(Color.argb(255, rgb1.getPwm1(), rgb1.getPwm2(), rgb1.getPwm3()));
        color2.setBackgroundColor(Color.argb(255, rgb2.getPwm1(), rgb2.getPwm2(), rgb2.getPwm3()));

        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(mainActivity)
                        .setTitle("Choose color")
                        .initialColor(((ColorDrawable)color1.getBackground()).getColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //Toast.makeText(mainActivity,"onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //changeBackgroundColor(selectedColor);

                                color1.setBackgroundColor(selectedColor);
                                rgb1.setPwm1(Color.red(selectedColor));
                                rgb1.setPwm2(Color.green(selectedColor));
                                rgb1.setPwm3(Color.blue(selectedColor));
                                inventory.saveProfileDevice(rgb1);
                                try {
                                    JSONObject jO = new JSONObject();
                                    jO.put("rgb1.R", Color.red(selectedColor));
                                    jO.put("rgb1.G", Color.green(selectedColor));
                                    jO.put("rgb1.B", Color.blue(selectedColor));
                                    sendJSON(jO);

                                }catch (JSONException o){}


                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(mainActivity)
                        .setTitle("Choose color")
                        .initialColor(((ColorDrawable)color2.getBackground()).getColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //Toast.makeText(mainActivity,"onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //changeBackgroundColor(selectedColor);
                                color2.setBackgroundColor(selectedColor);
                                rgb2.setPwm1(Color.red(selectedColor));
                                rgb2.setPwm2(Color.green(selectedColor));
                                rgb2.setPwm3(Color.blue(selectedColor));
                                inventory.saveProfileDevice(rgb2);
                                try {
                                    JSONObject jO = new JSONObject();

                                    jO.put("rgb2.R", Color.red(selectedColor));
                                    jO.put("rgb2.G", Color.green(selectedColor));
                                    jO.put("rgb2.B", Color.blue(selectedColor));
                                    sendJSON(jO);
                                }catch (JSONException o){}


                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });


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
                inventory.saveProfileDevice(dimm1);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("dimm1.status1", isChecked);
                    sendJSON(jO);

                }catch (JSONException o){}

            }
        });
        dimm2sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dimm2.setStatus1(isChecked);
                inventory.saveProfileDevice(dimm2);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("dimm2.status1", isChecked);
                    sendJSON(jO);

                }catch (JSONException o){}

            }
        });

        rgb1sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rgb1.setStatus1(isChecked);
                inventory.saveProfileDevice(rgb1);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("rgb1.status1", rgb1.getStatus1());
                    sendJSON(jO);
                }catch (JSONException o){}

            }
        });
        rgb2sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rgb2.setStatus1(isChecked);
                inventory.saveProfileDevice(rgb2);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("rgb2.status1", rgb2.getStatus1());
                    sendJSON(jO);
                }catch (JSONException o){}

            }
        });


        dimm1chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dimm1.setStatus2(isChecked);
                inventory.saveProfileDevice(dimm1);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("dimm1.status2", dimm1.getStatus2());
                    sendJSON(jO);

                }catch (JSONException o){}

            }
        });
        dimm2chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dimm2.setStatus2(isChecked);
                inventory.saveProfileDevice(dimm2);
                try {
                    JSONObject jO = new JSONObject();
                    jO.put("dimm2.status2", dimm1.getStatus2());
                    sendJSON(jO);

                }catch (JSONException o){}


            }
        });

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




    //private class BtBackgroundTask extends AsyncTask<JSONObject, String, Void> {
    //    private InputStream mmInStream = null;
    //    private OutputStream mmOutStream = null;
    //    BufferedReader br;
    //    BufferedWriter bw;
//
    //    public BtBackgroundTask(BluetoothSocket bluetoothSocket) {
//
    //        try {
    //            this.mmInStream = btSocket.getInputStream();
    //            this.mmOutStream = btSocket.getOutputStream();
    //            br = new BufferedReader(new InputStreamReader(mmInStream));
    //            bw = new BufferedWriter(new OutputStreamWriter(mmOutStream));
//
//
    //        }catch (IOException e){
//
//
    //        }
    //    }
//
    //    @Override
    //    protected Void doInBackground(JSONObject... params) {
    //        try {
    //            Log.d("Primera ejecucion BT", "true");
    //            while (!isCancelled()) {
    //                String readMessage = br.readLine();
    //                //if(sendMessageFlag)
    //                //{
//////
    //                //for(JSONObject jsonObject : jsonObjects)
    //                //{
    //                //    mmOutStream.flush();
    //                //    mmOutStream.write((jsonObject.toString()+"\r").getBytes());
    //                //}
    //                //    bw.flush();
    //                //    bw.write(((JSONObject)params[0]).toString());
////
    //                //    sendMessageFlag=false;
    //                //}
    //                //publishProgress(readMessage);
    //            }
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        return null;
    //    }
//
    //    @Override
    //    protected void onProgressUpdate(String... values) {
    //        if(jObj!=null) {
    //            textprobando.setText(jObj.toString());
    //        }
//
    //    }
    //}

    //@Override
    //public void onDestroy() {
    //    super.onDestroy();
    //    if(btThread!=null)
    //    {
    //        btThread.cancel(true);
    //    }
    //}
}
