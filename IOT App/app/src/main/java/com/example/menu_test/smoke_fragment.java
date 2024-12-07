package com.example.menu_test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class smoke_fragment extends Fragment {

    private SeekBar seekBar;                   //SeekBar
    private TextView seekBarTextView;          //SeekBar Value
    private TextView smokeTextView;            //SensorType
    private Button smokeButton;                 //Button to send infos to edge server

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_smoke_fragment, container, false);

        seekBar = view.findViewById(R.id.seekBar);
        seekBarTextView = view.findViewById(R.id.SeekBarTextView);
        smokeTextView = view.findViewById(R.id.smokeTextView);
        smokeButton = view.findViewById(R.id.smoke_button);

        seekBar.setMax(25);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                double smokeValue = progress / 100.0;
//                seekBarTextView.setText(String.format("Current Smoke Sensor Value: %.2f", smokeValue));
//                seekBar.setVisibility(View.VISIBLE);
                seekBarTextView.setVisibility(TextView.VISIBLE);
                updateSmokeSensor(progress / 1000.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        smokeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Smoke Sensor Activated", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateSmokeSensor(double value) {
        seekBarTextView.setText(String.format(Locale.US, "%.3f", value));
        if (value > 0.14) {
            seekBarTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }else {
            seekBarTextView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}