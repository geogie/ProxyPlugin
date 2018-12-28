package com.george.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.george.plugin.Constants;
import com.george.plugin.PluginManager;
import com.george.plugin.VLog;

import java.io.File;

public class MainActivity extends Activity {
    public final static String PLUGIN_NAME = "plugin.apk";
    public final static String PLUGIN_PACKAGE_NAME = "com.george.pluginapp";
    public final static String PLUGIN_CLAZZ_NAME = "com.george.pluginapp.MainActivity";
    private PluginManager mPluginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Constants.PACKAGE_NAME, PLUGIN_PACKAGE_NAME);
                intent.putExtra(Constants.PLUGIN_CLASS_NAME, PLUGIN_CLAZZ_NAME);
                mPluginManager.startActivity(intent);
            }
        });
        Utils.verifyStoragePermissions(this);

        PluginManager.init(getApplicationContext());// 初始化插件加载器
        mPluginManager = PluginManager.getInstance();
        String pluginApkPath = Environment.getExternalStorageDirectory() + File.separator + "plugins" + File.separator + PLUGIN_NAME;// 插件apk路径
        VLog.log("can read: " + Environment.getExternalStorageDirectory().canRead());
        VLog.log(pluginApkPath);
        mPluginManager.loadApk(pluginApkPath);// 插件apk加载进主app中
    }
}

