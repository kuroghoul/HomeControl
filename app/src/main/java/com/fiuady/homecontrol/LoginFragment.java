package com.fiuady.homecontrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fiuady.homecontrol.db.Inventory;

public class LoginFragment extends Fragment {


    private Button signupButton;
    private Button signinButton;
    private Button testButton;

    private EditText usertxt;
    private EditText passwordtxt;

    private String username;
    private String password;

    private TextView signinAttempt;

    private Inventory inventory;

    private MainFragment mainFragment;
    private ConnectFragment connectFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        inventory = new Inventory(getActivity());
        mainFragment = new MainFragment();

        connectFragment = new ConnectFragment();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        signinButton = (Button)view.findViewById(R.id.signin_button);
        signupButton = (Button)view.findViewById(R.id.signup_button);
        testButton = (Button)view.findViewById(R.id.testButton);
        signinAttempt = (TextView)view.findViewById(R.id.signin_attempt_txt);
        usertxt = (EditText)view.findViewById(R.id.user_edittext);
        passwordtxt = (EditText)view.findViewById(R.id.password_edittext);


        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usertxt.getText().toString();
                password = passwordtxt.getText().toString();

                switch (inventory.attemptLogin(username, password))
                {
                    case InvalidUser:
                        signinAttempt.setText("Usuario no encontrado");
                        break;
                    case WrongPassword:
                        signinAttempt.setText("Contraseña incorrecta");
                        break;
                    case Success:
                        signinAttempt.setText("Inicio de sesión exitoso");
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, mainFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                }
            }
        });



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewBt newBt = new NewBt();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, newBt).addToBackStack(null).commit();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFragment testfragmento = new testFragment();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, testfragmento);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }





}
