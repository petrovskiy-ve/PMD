package com.example.javaphongmodelapp;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {
    private Sphere sphere;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float mRotationAngle = 0.0f;
    private float mLightOffsetX = 0.1f;
    private float mLightDirection = 1;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error code: " + error);
        }

        sphere = new Sphere(150, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Настройка матрицы вида (камеры)
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 3.0f; // Положение камеры (0, 0, 3)
        float centerX = 0.0f;
        float centerY = 0.0f;
        float centerZ = 0.0f; // Направление, смотрящее на сферу (0, 0, 0)
        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        Matrix.rotateM(mMVPMatrix, 0, mRotationAngle, 0, 1, 0);

        // Параметры освещения
        float lightX = -10.0f + mLightOffsetX;
        float lightY = 0.0f;
        float lightZ = -3.0f;

        float[] lightPosition = {lightX * 1000, lightY * 1000, lightZ * 1000};
        float[] ambientColor = {0.6f, 0.3f, 0.7f};
        float[] diffuseColor = {0.3f, 0.3f, 0.3f};
        float[] specularColor = {1.0f, 1.0f, 1.0f};
        float[] objectColor = {1.0f, 1.0f, 1.0f};
        float shininess = 500.0f;

        sphere.draw(mMVPMatrix, lightPosition, ambientColor, diffuseColor, specularColor, shininess, objectColor);

        mRotationAngle += 1.0f;

        float lightStep = 0.1f;
        mLightOffsetX += mLightDirection * lightStep;

        if (mLightOffsetX >= 20.0f || mLightOffsetX <= 0.0f) {
            mLightDirection *= -1;
        }
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // Проверяем статус компиляции шейдера
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            // Компиляция завершилась неудачно, получаем журнал компиляции
            String shaderInfoLog = GLES20.glGetShaderInfoLog(shader);
            Log.e("Shader Compilation Error", "Error compiling shader: " + shaderInfoLog);

            // Здесь вы можете добавить оповещение на экран или выполнить другие действия по обработке ошибки
        } else {
            Log.d("Shader Compilation", "Shader compiled successfully");
        }

        return shader;
    }

}

