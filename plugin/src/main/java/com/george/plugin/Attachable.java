package com.george.plugin;

public interface Attachable<T> {
    void attach(T proxy, PluginApk apk);
}

