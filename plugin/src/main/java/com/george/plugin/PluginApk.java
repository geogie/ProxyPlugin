package com.george.plugin;

import android.content.pm.PackageInfo;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

/**
 * 实体：插件Apk文件
 */
public class PluginApk {
    public PackageInfo packageInfo;
    public DexClassLoader classLoader;
    public Resources pluginRes;

    public PluginApk(Resources pluginRes) {
        this.pluginRes = pluginRes;
    }

}
