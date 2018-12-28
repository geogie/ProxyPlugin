package com.george.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

/**
 * 实现插件的加载
 */
public class PluginManager {
    static class PluginMgrHolder {
        static PluginManager sManager = new PluginManager();
    }

    private static Context mContext;

    Map<String, PluginApk> sMap = new HashMap<>();

    public static PluginManager getInstance() {
        return PluginMgrHolder.sManager;
    }
    public PluginApk getPluginApk(String packageName) {
        return sMap.get(packageName);
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public final void loadApk(String apkPath) {
        PackageInfo packageInfo = queryPackageInfo(apkPath);
        if (packageInfo == null || TextUtils.isEmpty(packageInfo.packageName)) {
            return;
        }
        // check cache
        PluginApk pluginApk = sMap.get(packageInfo.packageName);

        if (pluginApk == null) {
            pluginApk = createApk(apkPath);
            if (pluginApk != null) {
                pluginApk.packageInfo = packageInfo;
                sMap.put(packageInfo.packageName, pluginApk);
            } else {
                throw new NullPointerException("PluginApk is null");
            }
        }
    }

    /**
     *
     * @param apkPath apk路径
     * @return pluginApk
     */
    private PluginApk createApk(String apkPath) {
        String addAssetPathMethod = "addAssetPath";
        PluginApk pluginApk = null;
        try { // 资源加载
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod(addAssetPathMethod, String.class);
            addAssetPath.invoke(assetManager, apkPath);// 把资源通过反射加载进去
            Resources pluginRes = new Resources(assetManager, mContext.getResources().getDisplayMetrics(), mContext.getResources().getConfiguration());
            pluginApk = new PluginApk(pluginRes);
            pluginApk.classLoader = createDexClassLoader(apkPath);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return pluginApk;
    }
    private PackageInfo queryPackageInfo(String apkPath) {
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            return null;
        }
        return packageInfo;
    }

    /**
     * 类加载：
     * 外部dex文件，通过 DexClassLoader 类加载。
     * @param apkPath apk路径
     * @return DexClassLoader对象
     */
    private DexClassLoader createDexClassLoader(String apkPath) {
        File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
        return new DexClassLoader(apkPath, dexOutputDir.getAbsolutePath(), null, mContext.getClassLoader());
    }

    /**
     * 启动插件中的activity
     * @param intent
     */
    public void startActivity(Intent intent) {
        Intent pluginIntent = new Intent(mContext, ProxyActivity.class);
        Bundle extra = intent.getExtras();
        // complicate if statement
        if (extra == null || !extra.containsKey(Constants.PLUGIN_CLASS_NAME) && !extra.containsKey(Constants.PACKAGE_NAME)) {
            try {
                throw new IllegalAccessException("lack class of plugin and package name");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        pluginIntent.putExtras(intent);
        pluginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(pluginIntent);
    }
}
