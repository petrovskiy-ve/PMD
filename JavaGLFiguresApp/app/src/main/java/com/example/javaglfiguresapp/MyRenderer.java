package com.example.javaglfiguresapp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyRenderer implements GLSurfaceView.Renderer {
    Context context;

    private final Square mSquare;
    private final Cube mCube;
    private final Sphere mSphere;


    private static float angleCube = 0;
    private float mAngle = 0;

    private int currentFigure = 0; // Индекс текущей фигуры: 0 - куб, 1 - сфера, 2 - прямоугольник

    public MyRenderer(Context context){
        this.context = context;
        mSquare = new Square();
        mCube = new Cube();
        mSphere = new Sphere(10);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;
        float aspect = (float)width / height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);
        gl.glMatrixMode(GL10.GL_MODELVIEW); // Select model-view matrix
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glRotatef(mAngle, 0, 0, -1);

        // Отрисовка текущей фигуры
        switch (currentFigure) {
            case 0: // Куб
                gl.glTranslatef(0.0f, 0.0f, -2.0f);
                gl.glScalef(0.6f, 0.6f, 0.6f);
                gl.glRotatef(angleCube, 1.0f, 1.0f, 0.5f);
                mCube.draw(gl);
                break;
            case 1: // Сфера
                //gl.glTranslatef(0f, 1.0f, -4.5f);
                gl.glScalef(0.05f, 0.05f, 0.05f);
                gl.glRotatef(mAngle, 1, 1, -1);
                mSphere.draw(gl);
                break;
            case 2: // Прямоугольник
                gl.glTranslatef(0.0f, 0.0f, -5.0f);
                gl.glRotatef(mAngle, 0, 0, -1);
                mSquare.draw(gl);
                break;
        }

        float speedCube = -1.5f;
        angleCube += speedCube;
        mAngle += 1;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // При нажатии на экран переключаемся на следующую фигуру
            currentFigure = (currentFigure + 1) % 3; // 0, 1, 2 - куб, сфера, прямоугольник
        }
        return true;
    }
}
