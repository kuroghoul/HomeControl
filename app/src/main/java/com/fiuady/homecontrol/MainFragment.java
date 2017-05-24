package com.fiuady.homecontrol;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MainFragment extends Fragment {


    private ImageButton illuminationBtn;
    private ImageButton doorsWindowsBtn;
    private ImageButton alarmBtn;
    private ImageButton movementBtn;
    private ImageButton temperatureBtn;
    private ImageButton profileBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.main_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        illuminationBtn = (ImageButton)view.findViewById(R.id.illumination_btn);
        doorsWindowsBtn = (ImageButton)view.findViewById(R.id.doors_and_windows_btn);
        alarmBtn = (ImageButton)view.findViewById(R.id.alarm_btn);
        movementBtn = (ImageButton)view.findViewById(R.id.movement_detector_btn);
        temperatureBtn = (ImageButton)view.findViewById(R.id.temperature_control_btn);
        profileBtn = (ImageButton)view.findViewById(R.id.my_profile_btn);


        illuminationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                IlluminationFragment illuminationFragment = new IlluminationFragment();
                ft.replace(R.id.fragment_container, illuminationFragment).addToBackStack(null).commit();
            }
        });
        doorsWindowsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DoorsFragment doorsFragment = new DoorsFragment();
                ft.replace(R.id.fragment_container, doorsFragment).addToBackStack(null).commit();
            }
        });
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                AlarmFragment doorsFragment = new AlarmFragment();
                ft.replace(R.id.fragment_container, doorsFragment).addToBackStack(null).commit();
            }
        });
        movementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        temperatureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
