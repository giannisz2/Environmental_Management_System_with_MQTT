package com.example.menu_test;

import android.annotation.SuppressLint;
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


public class gas_fragment extends Fragment {

    private SeekBar seekBar;                //SeekBar
    private TextView seekBarTextView;       //SeekBar Value
    private TextView gasTextView;           //SensorType
    private Button gasButton;               //Button to send infos to edge server


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gas_fragment, container, false);

        seekBar = view.findViewById(R.id.seekBarGas);
        seekBarTextView = view.findViewById(R.id.SeekBarTextView);
        gasTextView = view.findViewById(R.id.gasTextView);
        gasButton = view.findViewById(R.id.gas_button);

        seekBar.setMax(11);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

        gasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Gas Sensor Activated", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateSmokeSensor(double value) {
        seekBarTextView.setText(String.format(Locale.US, "%.3f", value));
        if (value > 9.15) {
            seekBarTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }else {
            seekBarTextView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}