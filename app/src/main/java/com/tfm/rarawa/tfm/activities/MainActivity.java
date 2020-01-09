package com.tfm.rarawa.tfm.activities;

import android.os.Bundle;

import com.tfm.rarawa.tfm.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.prev_activity_main);
        displayDrawer(); // Displays the toolbar and the drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
    }

}
