package com.example.jniapp;

import static com.example.jniapp.JNIWrapper.ondrawframe;
import static com.example.jniapp.JNIWrapper.onsurfacechanged;
import static com.example.jniapp.JNIWrapper.onsurfacecreated;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RendererWrapper implements GLSurfaceView.Renderer {
    private Context context;

    public RendererWrapper(Context context) {
        this.context = context;
    }

    private void loadGLTexture(GL10 gl) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        android.content.res.Resources resources = context.getResources();
        int resourceId = R.drawable.texture; // Замените на нужный идентификатор ресурса
        android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(resources, resourceId);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        loadGLTexture(gl);
        onsurfacecreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        onsurfacechanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        ondrawframe();
    }
}

