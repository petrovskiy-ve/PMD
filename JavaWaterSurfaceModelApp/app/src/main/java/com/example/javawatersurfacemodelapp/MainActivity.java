package com.example.javawatersurfacemodelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyRenderer render = new MyRenderer(this);
        super.onCreate(savedInstanceState);
        GLSurfaceView canvas = new GLSurfaceView(this);
        canvas.setEGLContextClientVersion(2);
        canvas.setRenderer(render);
        canvas.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(canvas);
    }
}