package com.example.solarsistem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyRenderer implements GLSurfaceView.Renderer {
    static public int[] texture_name = {
            R.drawable.sun,
            R.drawable.earth,
            R.drawable.moon
    };
    static public int[] textures = new int [texture_name.length];

    Context context;

    private Sphere Sun;
    private Sphere Earth;
    private Sphere Moon;

    private float sunRotate = 0f;
    private float earthRotate = 0f;
    private float moonRotate = 0f;
    private float earthSpeed = 0;
    private float moonSpeed = 0;

    public MyRenderer(Context context){
        this.context = context;
        Sun = new Sphere(6f);
        Earth = new Sphere(1f);
        Moon = new Sphere(0.5f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0,1.0f);
        gl.glClearDepthf(1);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-10,10, -10, 10, -10, 10);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glScalef(2f, 1f, 1);
        loadGLTexture(gl);
    }

    private void loadGLTexture(GL10 gl) {
        gl.glGenTextures(3, textures, 0);
        for (int i = 0; i < texture_name.length; ++i) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            InputStream is = context.getResources().openRawResource(texture_name[i]);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float RotationOffset;
        float RotationSpeed;
        sunRotate = (sunRotate == 360) ? 0 : (float) (sunRotate + 0.2);
        earthRotate = (earthRotate == 360) ? 0 : (float) (earthRotate + 1);
        moonRotate = (moonRotate == 360) ? 0 : (float) (moonRotate + 2);
        earthSpeed = (earthSpeed == 360) ? 0 : earthSpeed + 0.35f;
        moonSpeed = (moonSpeed == 360) ? 0 : moonSpeed + 0.45f;
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, Sun.textureBuffer);

        gl.glPushMatrix();
        gl.glRotatef(70, 1f, -0.2f, 0);
        gl.glRotatef(sunRotate, 0, 0, 0.1f);
        gl.glColor4f(1, 1 ,0 , 1);
        gl.glScalef(0.3f, 0.3f, 0.3f);
        Sun.onDrawFrame(gl);
        gl.glPopMatrix();

        RotationOffset = 4.0f;
        RotationSpeed = 0.05f;
        gl.glPushMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(RotationOffset * (float)(Math.cos(earthSpeed * RotationSpeed)),
                 0,
                RotationOffset * (float)(Math.sin(earthSpeed * RotationSpeed)));
        gl.glRotatef(90,1f, 0.6f, 0);
        gl.glRotatef(earthRotate, 0, 0, 2);
        gl.glPushMatrix();
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, Earth.textureBuffer);
        gl.glColor4f(1, 1 ,1 , 1);
        Earth.onDrawFrame(gl);
        gl.glRotatef(-earthRotate, 0.3f, 1, 0);
        gl.glPopMatrix();


        RotationOffset = 2f;
        RotationSpeed = 0.5f;
        gl.glTranslatef(RotationOffset * (float)(Math.cos(earthSpeed * RotationSpeed)),
                RotationOffset * (float)(Math.sin(earthSpeed * RotationSpeed)),
                0);
        gl.glRotatef(moonRotate, 1f, 0f, 0f);

        gl.glColor4f(0, 1f ,0f , 1);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, Moon.textureBuffer);
        gl.glColor4f(1, 1 ,1, 1);
        Moon.onDrawFrame(gl);

        gl.glPopMatrix();
        gl.glPopMatrix();

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

}