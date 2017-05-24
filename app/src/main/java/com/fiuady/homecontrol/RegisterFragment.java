package com.fiuady.homecontrol;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fiuady.homecontrol.R;
import com.fiuady.homecontrol.db.Inventory;

import io.apptik.widget.MultiSlider;

/**
 * Created by Kuro on 21/05/2017.
 */

public class RegisterFragment extends Fragment {

    private Button registerButton;
    private Button cancelButton;

    private EditText userNameTxt;
    private EditText passwordTxt;
    private EditText nipTxt;

    private Inventory inventory;
    private MainActivity mainActivity;
    private MultiSlider multiSlider;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        inventory = new Inventory(mainActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerButton = (Button)view.findViewById(R.id.register_button);
        cancelButton = (Button)view.findViewById(R.id.cancel_register_button);

        userNameTxt = (EditText)view.findViewById(R.id.register_username_et);
        passwordTxt = (EditText)view.findViewById(R.id.register_password_et);
        nipTxt = (EditText)view.findViewById(R.id.register_nip_et);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(inventory.attemptRegister(userNameTxt.getText().toString(),
                        passwordTxt.getText().toString(),
                        nipTxt.getText().toString()))
                {
                    case DuplicatedUser:
                        Toast.makeText(mainActivity, "Ya existe un usuario con ese nombre", Toast.LENGTH_LONG).show();
                        break;
                    case Success:
                        Toast.makeText(mainActivity, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show();
                        mainActivity.hideKeyboard(mainActivity, v);
                        getFragmentManager().popBackStackImmediate();
                        break;
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

    }
}
