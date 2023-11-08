package com.example.jniapp;

public class JNIWrapper {
    public static native void onsurfacecreated();
    public static native void onsurfacechanged(int width, int height);
    public static native void ondrawframe();

    static {
        System.loadLibrary("native-lib");
    }
}