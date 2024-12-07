package com.example.menu_test;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.new_sensor) {
//            Toast.makeText(this, "You have clicked New Sensor", Toast.LENGTH_SHORT).show();
            showNewSensorDialog();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }


    TabLayout tabLayout;
    ViewPager2 viewPager2;
    VPAdapter vpAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tablayout);
        viewPager2 = findViewById(R.id.viewpager);
        vpAdapter = new VPAdapter(this);
        viewPager2.setAdapter(vpAdapter);
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager2.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(vpAdapter.getPageTitle(position))
        ).attach();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showNewSensorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle("New Sensor");
        builder.setView(inflater.inflate(R.layout.dialog_new_sensor, null)).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog alertDialog = (AlertDialog) dialog;
                EditText sensorTypeEditText = alertDialog.findViewById(R.id.sensor_type_edittext);
                EditText sensorValuesEditText = alertDialog.findViewById(R.id.sensor_value_edittext);

                if(sensorTypeEditText != null && sensorValuesEditText != null) {
                    String sensorType = sensorTypeEditText.getText().toString();
                    String sensorValues = sensorValuesEditText.getText().toString();
                    //Toast.makeText(MainActivity.this, "Sensor Type: "+ sensorType + "\nValues: "+ sensorValues, Toast.LENGTH_SHORT).show();

                    if(!sensorType.isEmpty() && !sensorValues.isEmpty()) {
                        Fragment newFragment = null;

                        if("Smoke".equalsIgnoreCase(sensorType)) {
                            newFragment = new smoke_fragment();
                        }else if("Gas".equalsIgnoreCase(sensorType)) {
                            newFragment = new gas_fragment();
                        }else if("UV".equalsIgnoreCase(sensorType)) {
                            newFragment = new uv_fragment();
                        }

                        if(newFragment != null) {
                            vpAdapter.addFragment(newFragment, sensorType);

                            new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(vpAdapter.getPageTitle(position))).attach();

                            viewPager2.setCurrentItem(vpAdapter.getItemCount() - 1, true);
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}