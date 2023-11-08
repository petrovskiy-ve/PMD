package com.example.lab6

import android.app.Activity
import android.app.ActivityManager
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.widget.Toast


class MainActivity : Activity() {
    private var glSurfaceView: GLSurfaceView? = null
    private var rendererSet = false
    private val isProbablyEmulator: Boolean
        get() = (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000 || isProbablyEmulator
        if (supportsEs2) {
            glSurfaceView = GLSurfaceView(this)
            if (isProbablyEmulator) {
                // Avoids crashes on startup with some emulator images.
                glSurfaceView?.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            }
            glSurfaceView?.setRenderer(RendererWrapper(this))
            rendererSet = true
            setContentView(glSurfaceView)
        } else {
            // Should never be seen in production, since the manifest filters
            // unsupported devices.
            Toast.makeText(
                this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            glSurfaceView?.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            glSurfaceView?.onResume()
        }
    }
}