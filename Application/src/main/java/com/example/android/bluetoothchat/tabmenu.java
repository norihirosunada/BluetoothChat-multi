package com.example.android.bluetoothchat;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.widget.TabHost;


/**
 * Created by norihirosunada on 15/09/26.
 */
public class tabmenu extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmenu);

        FragmentTabHost tabhost = (FragmentTabHost)findViewById(R.id.tabHost);
        tabhost.setup(this,getSupportFragmentManager(),R.id.sample_content_fragment);
        TabHost.TabSpec tabspecs;
        Intent intent;

        intent = new Intent().setClass(this,DeviceListActivity.class);
        tabspecs = tabhost.newTabSpec("tab1");
        tabspecs.setIndicator("first");
        tabspecs.setContent(intent);
        tabhost.addTab(tabspecs);

        intent = new Intent().setClass(this,MainActivity.class);
        tabspecs = tabhost.newTabSpec("tab2");
        tabspecs.setIndicator("second").setContent(intent);
        tabhost.addTab(tabspecs);
    }
}
