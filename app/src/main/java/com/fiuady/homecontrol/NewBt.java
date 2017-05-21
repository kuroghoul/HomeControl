package com.fiuady.homecontrol;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Kuro on 21/05/2017.
 */

public class NewBt extends Fragment{

    private static final int REQUEST_ENABLE_BT = 1;

    private BtDevRvAdapter adapter;
    private List<BluetoothDevice> devices;

    private EditText txtState;

    private MainActivity mainactivity;
    private BluetoothSocket connectedSocket = null;

    Button pairedButton;
    Button discoverButton;
    Button disconnectButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newbt, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainactivity = (MainActivity)getActivity();

        // Setup devices recycler-view
        RecyclerView rvDevices = (RecyclerView) view.findViewById(R.id.devices_list);
        rvDevices.setLayoutManager(new LinearLayoutManager(getActivity()));

        devices = new ArrayList<>();
        adapter = new BtDevRvAdapter(devices);
        rvDevices.setAdapter(adapter);

        // Setup state multiline edit-text
        txtState = (EditText) view.findViewById(R.id.state_text);
        txtState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(),  view.findViewById(R.id.state_text_label));
                popup.getMenuInflater().inflate(R.menu.clear_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.erase_menu_item:
                                // Clear state text-edit
                                txtState.setText("");
                                break;
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

        pairedButton = (Button)view.findViewById(R.id.paired_button);
        discoverButton = (Button)view.findViewById(R.id.discover_button);
        disconnectButton = (Button)view.findViewById(R.id.disconnect_button);
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
         pairedButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 devices.clear();
                 adapter.update();

                 // Get paired devices
                 appendStateText("[Acción] Buscando dispositivos sincronizados...");
                 Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

                 // Check if there are paired devices
                 if (pairedDevices.size() > 0) {
                     if (pairedDevices.size() == 1) {
                         appendStateText("[Info] Se encontró 1 dispositivo.");
                     } else {
                         appendStateText("[Info] Se encontraron " + pairedDevices.size() + " dispositivos.");
                     }

                     // Loop through paired devices
                     for (BluetoothDevice device : pairedDevices) {
                         devices.add(device);
                         appendStateText("[Info] Dispositivo sincronizado: " + device.getName() + ".");
                     }

                     adapter.update();
                 } else {
                     appendStateText("[Info] No se encontraron dispositivos sincronizados.");
                 }
             }
         });
         discoverButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // Check if device supports Bluetooth
                 if (btAdapter == null) {
                     appendStateText("[Error] Dispositivo Bluetooth no encontrado!");
                 }
                 // Check if device adapter is not enabled
                 else if (!btAdapter.isEnabled()) {
                     // Issue a request to enable Bluetooth through the system settings (without stopping application)
                     Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                     startActivityForResult(intent, REQUEST_ENABLE_BT);
                 }
                 // Start discovery process
                 else {
                     devices.clear();
                     adapter.update();
                     btAdapter.startDiscovery();
                 }
             }
         });
         disconnectButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (connectedSocket != null) {
                     try {
                         connectedSocket.close();
                     } catch (IOException e) {
                         appendStateText("[Error] Ocurrió un problema al intentar cerrar la conexión!");
                         e.printStackTrace();
                     } finally {
                         connectedSocket = null;
                         appendStateText("[Estado] Desconectado.");
                     }

                 } else {
                     appendStateText("[Info] La conexión no parece estar activa.");
                 }
             }
         });

// Register for broadcasts when bluetooth device state changes
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(btStateReceiver, filter);

        // Register for broadcasts when bluetooth discovery state changes
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        getActivity().registerReceiver(btDiscoveryStartedReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(btDiscoveryFinishedReceiver, filter);

        // Register for broadcasts when a device is discovered.
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(btFoundReceiver, filter);
    }



    // ************************************************
    // BtBackgroundTask
    // ************************************************

    private class BtBackgroundTask extends AsyncTask<BufferedReader, String, Void> {
        @Override
        protected Void doInBackground(BufferedReader... params) {
            try {
                while (!isCancelled()) {
                    publishProgress(params[0].readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //appendMessageText("[Recibido] " + values[0]);
        }
    }

    // ************************************************
    // ViewHolder for RecyclerView
    // ************************************************

    private class BtDevRvHolder extends RecyclerView.ViewHolder {
        private final TextView lblName;
        private final TextView lblAddress;

        private BluetoothDevice device;

        BtDevRvHolder(View itemView) {
            super(itemView);

            lblName = (TextView) itemView.findViewById(R.id.device_name);
            lblAddress = (TextView) itemView.findViewById(R.id.device_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getActivity(), lblName);
                    popup.getMenuInflater().inflate(R.menu.device_popup, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.connect_menu_item:
                                    if(mainactivity.attemptConnection(device))
                                    {

                                            connectedSocket = mainactivity.getConnectedSocket();
                                            //BufferedReader br = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream()));
                                            //new BtBackgroundTask().execute(br);
                                            appendStateText("[Estado] Conexión exitosa.");

                                    }
                                    else
                                    {
                                        appendStateText("[Error] No se pudo establecer conexión!");
                                    }
                                    //***********************************************************************
                                    break;

                                default:
                                    break;
                            }

                            return true;
                        }
                    });

                    popup.show();
                }
            });
        }

        void bind(BluetoothDevice device) {
            this.device = device;
            lblName.setText(device.getName());
            lblAddress.setText(device.getAddress());
        }
    }


    // ************************************************
    // Adapter for RecyclerView
    // ************************************************

    private class BtDevRvAdapter extends RecyclerView.Adapter<BtDevRvHolder> {
        private List<BluetoothDevice> devices;

        BtDevRvAdapter(List<BluetoothDevice> devices) {
            this.devices = devices;
        }

        @Override
        public BtDevRvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new BtDevRvHolder(inflater.inflate(R.layout.device_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(BtDevRvHolder holder, int position) {
            holder.bind(devices.get(position));
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        void update() {
            notifyDataSetChanged();
        }
    }


// ************************************************
    // MainActivity implementation
    // ************************************************

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED
    private final BroadcastReceiver btStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                // Bluetooth adapter state has changed
                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                    case BluetoothAdapter.STATE_OFF:
                        appendStateText("[Estado] Apagado.");
                        break;

                    case BluetoothAdapter.STATE_ON:
                        appendStateText("[Estado] Encendido.");
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        appendStateText("[Acción] Apagando...");
                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        appendStateText("[Acción] Encendiendo...");
                        break;
                }
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_DISCOVERY_STARTED
    private final BroadcastReceiver btDiscoveryStartedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                appendStateText("[Acción] Iniciando búsqueda de dispositivos...");
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_DISCOVERY_FINISHED
    private final BroadcastReceiver btDiscoveryFinishedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                appendStateText("[Acción] Finalizando búsqueda de dispositivos...");
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver btFoundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                adapter.update();

                appendStateText("[Info] Dispositivo encontrado: " + device.getName() + ".");
            }
        }
    };

    private void appendStateText(String text) {
        txtState.setText(text + "\n" + txtState.getText());
    }

}
