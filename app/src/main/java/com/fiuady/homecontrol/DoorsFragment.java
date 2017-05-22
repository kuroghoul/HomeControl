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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.homecontrol.db.Inventory;
import com.fiuady.homecontrol.db.ProfileDevice;
import com.fiuady.homecontrol.db.User;
import com.fiuady.homecontrol.db.UserProfile;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Kuro on 22/05/2017.
 */

public class DoorsFragment extends Fragment {

    private BluetoothSocket btSocket = null;
    private BtBackgroundTask btThread = null;
    private boolean sendMessageFlag = false;

    private User user;
    private UserProfile profile;

    private JSONObject jObj;

    private MainActivity mainActivity;
    Inventory inventory;

    private ProfileDevice doorMain;
    private ProfileDevice door2;


    private EditText nipET;

    private ImageButton doorMainBtn;
    private ImageButton door2Btn;

    private TextView doorMainTxt;
    private TextView door2Txt;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity=(MainActivity)getActivity();
        user=mainActivity.getCurrentUser();
        profile=mainActivity.getCurrentUserProfile();
        inventory = new Inventory(mainActivity);

        doorMain = inventory.getProfileDevice(profile.getId(), user.getId());
        door2 = inventory.getProfileDevice(profile.getId(), user.getId());




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doors, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        nipET = (EditText)view.findViewById(R.id.door_main_nip_et);
        doorMainBtn = (ImageButton)view.findViewById(R.id.door_main_btn);
        doorMainTxt = (TextView)view.findViewById(R.id.door_main_txt);

        door2Btn = (ImageButton)view.findViewById(R.id.door_2_btn);
        door2Txt = (TextView)view.findViewById(R.id.door_2_txt);



        doorMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doorMain.getStatus1()) {
                    if (nipET.getText().toString().equals(user.getNip())) {
                        doorMainBtn.setBackgroundResource(R.drawable.unlocked);
                        doorMain.setStatus1(true);
                        nipET.setText("");
                        nipET.clearFocus();

                        mainActivity.hideKeyboard(mainActivity,v);
                        //----------------------PONER FUNCION PARA ABRIR PUERTA-----------------------------//
                    }
                    else
                    {
                        Toast.makeText(mainActivity, "NIP incorrecto", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    doorMainBtn.setBackgroundResource(R.drawable.hardlocked);
                    doorMain.setStatus1(false);

                }
            }
        });

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


}
