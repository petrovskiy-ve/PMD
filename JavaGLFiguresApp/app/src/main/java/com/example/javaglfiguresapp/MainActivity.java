package com.example.javaglfiguresapp;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private GLSurfaceView g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);

        g = new GLSurfaceView(this);
        g.setEGLConfigChooser(8, 8, 8, 8, 16, 1);
        final MyRenderer myRenderer = new MyRenderer(this); // Создайте экземпляр MyRenderer
        g.setRenderer(myRenderer);
        g.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // Добавьте обработчик события нажатия на экран
        g.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return myRenderer.onTouchEvent(event);
            }
        });

        setContentView(g);
    }

    @Override
    protected void onPause() {
        super.onPause();
        g.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        g.onResume();
    }
}
