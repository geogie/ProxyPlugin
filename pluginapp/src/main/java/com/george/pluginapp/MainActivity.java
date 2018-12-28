package com.george.pluginapp;

import android.os.Bundle;
import android.widget.ImageView;

import com.george.plugin.PluginActivity;

public class MainActivity extends PluginActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Plugin App");
        ((ImageView) findViewById(R.id.iv_logo)).setImageResource(R.mipmap.ic_launcher);
    }
}
