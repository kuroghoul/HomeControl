package com.fiuady.homecontrol;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * Created by Kuro on 21/05/2017.
 */

public class testFragment extends Fragment {

    private MainActivity mainActivity;
    JSONObject jObj = null;
    Button btnOn, btnOff;
    TextView txtArduino, txtString, txtStringLength, sensorView0, sensorView1, sensorView2, sensorView3;
    Handler bluetoothIn;
    private BluetoothSocket btSocket = null;
    private BtBackgroundTask btThread = null;
    private boolean sendMessageFlag = false;

    final int handlerState = 0;                        //used to identify handler message
    private StringBuilder recDataString = new StringBuilder();

    //private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    SeekBar seekBar;

    String sensor0="";
    String sensor1="";
    String sensor2="";
    String sensor3="";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_test,container,false );
    }


    private class BtBackgroundTask extends AsyncTask<BluetoothSocket, String, Void> {
        private  InputStream mmInStream = null;
        private  OutputStream mmOutStream = null;
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
                        //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(btSocket.getOutputStream()));
                        bw.flush();
                        bw.write(jObj.toString());
                        bw.flush();
                        sendMessageFlag=false;
                    }




                    publishProgress(readMessage);
                    //Log.d("Recibido= ", params[0].readLine());

                }
            } catch (IOException e) {
                Log.d("Primera ejecucion BT", "false");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            txtString.setText("Data Received = " + values[0]);
            try {
                jObj = new JSONObject(values[0]);
                sensor0 = jObj.getString("sensor0");
                sensor1 = jObj.getString("sensor1");
                sensor2 = jObj.getString("sensor2");
                sensor3 = jObj.getString("sensor3");


            } catch (JSONException o) {}


            sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");    //update the textviews with sensor values
            sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
            sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
            txtString.setText("Data Received = " + values[0]);


        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnOn = (Button) view.findViewById(R.id.buttonOn);
        btnOff = (Button) view.findViewById(R.id.buttonOff);
        txtString = (TextView) view.findViewById(R.id.txtString);
        txtStringLength = (TextView) view.findViewById(R.id.testView1);
        sensorView0 = (TextView) view.findViewById(R.id.sensorView0);
        sensorView1 = (TextView) view.findViewById(R.id.sensorView1);
        sensorView2 = (TextView) view.findViewById(R.id.sensorView2);
        sensorView3 = (TextView) view.findViewById(R.id.sensorView3);
        seekBar = (SeekBar)view.findViewById(R.id.newseekbar);

        //bluetoothIn = new Handler() {
        //    public void handleMessage(BufferedReader br) {
        //        String readMessage = br.toString();                             //if message is what we want
        //                                                                         // msg.arg1 = bytes from connect thread
        //            try {
        //                jObj = new JSONObject(readMessage);
        //                sensor0 = jObj.getString("sensor0");
        //                sensor1 = jObj.getString("sensor1");
        //                sensor2 = jObj.getString("sensor2");
        //                sensor3 = jObj.getString("sensor3");
        //            } catch (JSONException o) {
//
        //            }
        //                sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");    //update the textviews with sensor values
        //                sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
        //                sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
        //                sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
        //                txtString.setText("Data Received = " + readMessage);
//
        //            //recDataString.append(readMessage);                                      //keep appending to string until ~
        //            //int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
        //            //if (endOfLineIndex > 0) {                                           // make sure there data before ~
        //            //    String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
        //            //    txtString.setText("Data Received = " + dataInPrint);
        //            //    int dataLength = dataInPrint.length();                          //get length of data received
        //            //    txtStringLength.setText("String Length = " + String.valueOf(dataLength));
////
        //            //    if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
        //            //    {
        //            //        String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
        //            //        String sensor1 = recDataString.substring(6, 10);            //same again...
        //            //        String sensor2 = recDataString.substring(11, 15);
        //            //        String sensor3 = recDataString.substring(16, 17);
////
        //            //        sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");    //update the textviews with sensor values
        //            //        sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
        //            //        sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
        //            //        sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
        //            //    }
        //            //    recDataString.delete(0, recDataString.length());                    //clear all string dat// strIncom =" ";
        //            //    dataInPrint = " ";
        //            //}
//
        //    }
        //};

        //mainActivity.checkBTState();

        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        //btnOff.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        mConnectedThread.write("0");    // Send "0" via Bluetooth
        //        Toast.makeText(getActivity(), "Turn off LED", Toast.LENGTH_SHORT).show();
        //    }
        //});
//

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if ((btSocket != null) && (btSocket.isConnected())) {
                    jObj = new JSONObject();
                    try {
                        jObj.put("led", seekBar.getProgress());
                    }catch (JSONException e){}



                    //String toSend = txtToSend.getText().toString().trim();

                    // TBI - This object "should" be a member variable
                    //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(btSocket.getOutputStream()));
                    //bw.write(jObj.toString()+"\r");
                    //bw.flush();
                    sendMessageFlag = true;
                    //appendMessageText("[Enviado] " + toSend);


                    //txtToSend.setText("");
                } else {
                    //appendStateText("[Error] La conexión no parece estar activa!");
                }
                txtStringLength.setText(jObj.toString());
                //} catch (IOException e) {
                //    Toast.makeText(getActivity(), " Ocurrió un problema durante el envío de datos", Toast.LENGTH_LONG).show();
                //    //appendStateText("[Error] Ocurrió un problema durante el envío de datos!");
                //    e.printStackTrace();
                //}


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //try {

                if ((btSocket != null) && (btSocket.isConnected())) {
                    jObj = new JSONObject();
                    try {
                        jObj.put("led", seekBar.getProgress());
                    }catch (JSONException e){}



                    //String toSend = txtToSend.getText().toString().trim();

                    // TBI - This object "should" be a member variable
                    //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(btSocket.getOutputStream()));
                    //bw.write(jObj.toString()+"\r");
                    //bw.flush();
                    sendMessageFlag = true;
                    //appendMessageText("[Enviado] " + toSend);


                    //txtToSend.setText("");
                } else {
                    //appendStateText("[Error] La conexión no parece estar activa!");
                }
                txtStringLength.setText(jObj.toString());
                //} catch (IOException e) {
                //    Toast.makeText(getActivity(), " Ocurrió un problema durante el envío de datos", Toast.LENGTH_LONG).show();
                //    //appendStateText("[Error] Ocurrió un problema durante el envío de datos!");
                //    e.printStackTrace();
                //}
            }
        });
        btSocket = mainActivity.getConnectedSocket();
        try {
            if(btSocket!=null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));


                btThread = new BtBackgroundTask(btSocket);
                btThread.execute();
                Log.d("CREACIÓN DEL BT TASK = ", "true");
                //Log.d("BufferReader1 ", br.readLine());
            }
        }catch (IOException e){}






    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(btThread!=null)
        {
            btThread.cancel(true);
        }

        //mConnectedThread.interrupt();
    }

    //protected class ConnectedThread extends Thread {
    //    private final InputStream mmInStream;
    //    private final OutputStream mmOutStream;
//
    //    //creation of the connect thread
    //    public ConnectedThread(BluetoothSocket socket) {
    //        InputStream tmpIn = null;
    //        OutputStream tmpOut = null;
//
    //        try {
    //            //Create I/O streams for connection
    //            tmpIn = socket.getInputStream();
    //            tmpOut = socket.getOutputStream();
    //        } catch (IOException e) { }
//
    //        mmInStream = tmpIn;
    //        mmOutStream = tmpOut;
    //    }
//
    //    public void run() {
    //        try{
    //        BufferedReader br = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
    //            bluetoothIn.obtainMessage(handlerState, br);
    //        } catch (IOException e){}
//
//
//
    //        byte[] buffer = new byte[10240];
    //        int bytes;
//
    //        // Keep looping to listen for received messages
    //        while (true) {
    //            try {
    //                bytes = mmInStream.read(buffer);            //read bytes from input buffer
    //                String readMessage = new String(buffer, 0, bytes);
    //                // Send the obtained bytes to the UI Activity via handler
    //                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
    //            } catch (IOException e) {
    //                throw new ClassCastException("holasdasd");
    //                //break;
    //            }
    //        }
    //    }
    //    //write method
    //    public void write(String input) {
    //        byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
    //        try {
    //            mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
    //        } catch (IOException e) {
    //            //if you cannot write, close the application
    //            Toast.makeText(getActivity(), "Connection Failure", Toast.LENGTH_LONG).show();
    //            //finish();
//
    //        }
    //    }
    //}



}
