package com.fiuady.homecontrol;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;




import com.fiuady.homecontrol.db.Inventory;
import com.fiuady.homecontrol.db.ProfileDevice;
import com.fiuady.homecontrol.db.User;
import com.fiuady.homecontrol.db.UserProfile;

public class MainActivity extends AppCompatActivity {


    private static final UUID SERIAL_PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothSocket connectedSocket;

    protected User currentUser;
    protected UserProfile currentUserProfile;
    protected ArrayList<ProfileDevice> profileDevices;
    protected Inventory inventory;

    public ArrayList<ProfileDevice> getProfileDevices() {
        return profileDevices;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = null;
        currentUserProfile = null;
        inventory = new Inventory(this);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_container)!=null){
            if(savedInstanceState!=null)
            {
                return;
            }
            LoginFragment loginFragment = new LoginFragment();

            getFragmentManager().beginTransaction().add(R.id.fragment_container, loginFragment).commit();
        }

    }


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public UserProfile getCurrentUserProfile() {
        return currentUserProfile;
    }

    public void setCurrentUserProfile(UserProfile currentUserProfile) {
        this.currentUserProfile = currentUserProfile;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public BluetoothSocket getConnectedSocket ()
    {
        return connectedSocket;
    }

    public boolean attemptConnection (BluetoothDevice device)
    {
        // Use a temporary socket until correct connection is done
        BluetoothSocket tmpSocket = null;

        // Connect with BluetoothDevice
        if (connectedSocket == null) {
            try {
                tmpSocket = device.createRfcommSocketToServiceRecord(SERIAL_PORT_UUID);

                // Get device's own Bluetooth adapter
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                // Cancel discovery because it otherwise slows down the connection.
                btAdapter.cancelDiscovery();

                // Connect to the remote device through the socket. This call blocks until it succeeds or throws an exception
                tmpSocket.connect();

                // Acknowledge connected socket
                connectedSocket = tmpSocket;

                // Create socket reader thread
                //BufferedReader br = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream()));
                //new BtBackgroundTask().execute(br);

                //appendStateText("[Estado] Conectado.");
                return true;
            } catch (IOException e) {
                try {
                    if (tmpSocket != null) {
                        tmpSocket.close();
                    }
                } catch (IOException closeExceptione) {
                }

                //appendStateText("[Error] No se pudo establecer conexión!");
                e.printStackTrace();
                return false;

            }
        }
        else
        {
            return false;
        }
    }
    public void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void generateDevices()
    {
        if(currentUser!=null) {
            profileDevices = inventory.getAllDeviceProfiles(currentUserProfile.getId());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_connect:
                NewBt newBt= new NewBt();

                getFragmentManager().beginTransaction().add(R.id.fragment_container, newBt).commit();
                return true;





            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
