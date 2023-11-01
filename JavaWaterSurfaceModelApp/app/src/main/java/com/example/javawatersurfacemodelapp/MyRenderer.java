package com.example.javawatersurfacemodelapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
class MyRenderer implements GLSurfaceView.Renderer {
    Context c;
    private int[] texture = new int [2];
    private float x_camera, y_camera, z_camera;
    private float x_light_position, y_light_position, z_light_position;
    private float[] model_matrix;
    private float[] view_matrix;
    private float[] model_view_matrix;
    private float[] projection_matrix;
    private float[] model_view_projection_matrix;
    private int max_size_x = 100;
    private int max_size_z = 100;
    private int size_index;
    private float x0 =- 3.0f;
    private float z0 = -3.0f;
    private float dx = 0.05f;
    private float dz = 0.05f;
    private float [] x;
    private float [][] y;
    private float [] z;
    private float [] vertexes;
    private float [][] normales_x;
    private float [][] normales_y;
    private float [][] normales_z;
    private float [] normales;
    private FloatBuffer vertexes_buffer, normales_buffer;
    private ShortBuffer index_buffer;
    private MyShader m_shader;
    public MyRenderer(Context context) {
        c = context;
        x_light_position = 10f;
        y_light_position = 10f;
        z_light_position = 10f;
        model_matrix = new float [16];
        view_matrix = new float [16];
        model_view_matrix = new float [16];
        projection_matrix = new float [16];
        model_view_projection_matrix = new float [16];
        Matrix.setIdentityM(model_matrix, 0);
        x_camera = 3.0f;
        y_camera = 3.0f;
        z_camera = 0.0f;
        Matrix.setLookAtM(view_matrix, 0, x_camera, y_camera, z_camera, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(model_view_matrix, 0, view_matrix, 0, model_matrix, 0);
        x = new float [max_size_x + 1];
        z = new float [max_size_z + 1];
        y = new float [max_size_z + 1][max_size_x + 1];
        vertexes = new float [(max_size_z + 1) * (max_size_x + 1) * 3];
        normales_x = new float [max_size_z + 1][max_size_x + 1];
        normales_y = new float [max_size_z + 1][max_size_x + 1];
        normales_z = new float [max_size_z + 1][max_size_x + 1];
        normales = new float [(max_size_z + 1) * (max_size_x + 1) * 3];
        for (int i = 0; i <= max_size_x; i++) {
            x[i] = x0 + i * dx;
        }
        for (int j = 0; j <= max_size_z; j++) {
            z[j] = z0 + j * dz;
        }
        ByteBuffer vb = ByteBuffer.allocateDirect((max_size_z + 1) * (max_size_x + 1) * 3 * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexes_buffer = vb.asFloatBuffer();
        vertexes_buffer.position(0);
        ByteBuffer nb = ByteBuffer.allocateDirect((max_size_z + 1) * (max_size_x + 1) * 3 * 4);
        nb.order(ByteOrder.nativeOrder());
        normales_buffer = nb.asFloatBuffer();
        normales_buffer.position(0);
        short[] index;
        size_index = 2 * (max_size_x + 1) * max_size_z + (max_size_z - 1);
        index = new short [size_index];
        int k = 0;
        int j = 0;
        while (j < max_size_z) {
            for (int i = 0; i <= max_size_x; i++) {
                index[k] = chain(j, i);
                k++;
                index[k] = chain(j + 1, i);
                k++;
            }
            if (j < max_size_z - 1) {
                index[k] = chain(j + 1, max_size_x);
                k++;
            }
            j++;
            if (j < max_size_z) {
                for (int i = max_size_x; i >= 0; i--) {
                    index[k] = chain(j, i);
                    k++;
                    index[k] = chain(j + 1, i);
                    k++;
                }
                if (j < max_size_z - 1) {
                    index[k] = chain(j + 1,0);
                    k++;
                }
                j++;
            }
        }
        ByteBuffer bi = ByteBuffer.allocateDirect(size_index * 2);
        bi.order(ByteOrder.nativeOrder());
        index_buffer = bi.asShortBuffer();
        index_buffer.put(index);
        index_buffer.position(0);
        get_vertexes();
        get_normales();
    }
    private short chain(int j, int i) {
        return (short) (i + j * (max_size_x + 1));
    }
    private void get_vertexes() {
        double time = System.currentTimeMillis();
        for (int j = 0; j <= max_size_z; j++) {
            for (int i = 0; i <= max_size_x; i++){
                y[j][i] = 0.02f * (float) Math.cos(0.005 * time + 5 * (z[j] + x[i]));
            }
        }
        int k = 0;
        for (int j = 0; j <= max_size_z; j++) {
            for (int i = 0; i <= max_size_x; i++) {
                vertexes[k] = x[i];
                k++;
                vertexes[k] = y[j][i];
                k++;
                vertexes[k] = z[j];
                k++;
            }
        }
        vertexes_buffer.put(vertexes);
        vertexes_buffer.position(0);
    }
    private void get_normales() {
        for (int j = 0; j < max_size_z; j++) {
            for (int i = 0; i < max_size_x; i++) {
                normales_x[j][i] = -(y[j][i+1] - y[j][i]) * dz;
                normales_y[j][i] = dx * dz;
                normales_z[j][i] = -dx * (y[j+1][i] - y[j][i]);
            }
        }
        for (int j = 0; j < max_size_z; j++) {
            normales_x[j][max_size_x] = (y[j][max_size_x - 1] - y[j][max_size_x]) * dz;
            normales_y[j][max_size_x] = dx * dz;
            normales_z[j][max_size_x] = -dx * (y[j + 1][max_size_x] - y[j][max_size_x]);
        }
        for (int i = 0; i < max_size_x; i++) {
            normales_x[max_size_z][i] = -(y[max_size_z][i + 1] - y[max_size_z][i]) * dz;
            normales_y[max_size_z][i] = dx * dz;
            normales_z[max_size_z][i] = dx * (y[max_size_z - 1][i] - y[max_size_z][i]);
        }
        normales_x[max_size_z][max_size_x]= (y[max_size_z][max_size_x - 1] - y[max_size_z][max_size_x]) * dz;
        normales_y[max_size_z][max_size_x] = dx * dz;
        normales_z[max_size_z][max_size_x] = dx * (y[max_size_z - 1][max_size_x] - y[max_size_z][max_size_x]);
        int k = 0;
        for (int j = 0; j <= max_size_z; j++) {
            for (int i = 0; i <= max_size_x; i++) {
                normales[k] = normales_x[j][i];
                k++;
                normales[k] = normales_y[j][i];
                k++;
                normales[k] = normales_z[j][i];
                k++;
            }
        }
        normales_buffer.put(normales);
        normales_buffer.position(0);
    }
    @SuppressLint("ResourceType")
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glGenTextures(1, texture, 0);
        InputStream stream;
        Bitmap bitmap;
        stream = c.getResources().openRawResource(R.drawable.water);
        bitmap = BitmapFactory.decodeStream(stream);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        int backgroundTexture = loadBackgroundTexture(R.drawable.background);

        String vertex_shader =
                "uniform mat4 u_modelViewProjectionMatrix;"+
                        "attribute vec3 a_vertex;"+
                        "attribute vec3 a_normal;"+
                        "varying vec3 v_vertex;"+
                        "varying vec3 v_normal;"+
                        "void main() {"+
                        "v_vertex = a_vertex;"+
                        "vec3 n_normal = normalize(a_normal);"+
                        "v_normal = n_normal;"+
                        "gl_Position = u_modelViewProjectionMatrix * vec4(a_vertex, 1.0);"+
                        "}";
        String fragment_shader =
                "precision mediump float;" +
                        "uniform vec3 u_camera;" +
                        "uniform vec3 u_lightPosition;" +
                        "uniform sampler2D u_texture0;" +
                        "uniform sampler2D u_backgroundTexture;" +  // Текстура для фона
                        "varying vec3 v_vertex;" +
                        "varying vec3 v_normal;" +
                        "vec3 myrefract(vec3 IN, vec3 NORMAL, float k) {" +
                        " float nv = dot(NORMAL,IN);" +
                        " float v2 = dot(IN,IN);" +
                        " float knormal = (sqrt(((k * k - 1.0) * v2) / (nv * nv) + 1.0) - 1.0) * nv;" +
                        " vec3 OUT = IN + (knormal * NORMAL);" +
                        " return OUT;" +
                        "}" +
                        "void main() {" +
                        " vec3 n_normal = normalize(v_normal);" +
                        " vec3 lightvector = normalize(u_lightPosition - v_vertex);" +
                        " vec3 lookvector = normalize(u_camera - v_vertex);" +
                        " float ambient = 0.2;" +
                        " float k_diffuse = 0.8;" +
                        " float k_specular = 0.7;" +
                        " float diffuse = k_diffuse * max(dot(n_normal, lightvector), 0.0);" +
                        " vec3 reflectvector = reflect(-lightvector, n_normal);" +
                        " float specular = k_specular * pow( max(dot(lookvector,reflectvector),0.0), 40.0);" +
                        " vec4 one = vec4(1.0,1.0,1.0,1.0);" +
                        " vec4 lightColor = (ambient + diffuse + specular) * one;" +
                        " vec3 OUT = myrefract(-lookvector, n_normal, 1.2);" +
                        " float ybottom = -1.0;" +
                        " float xbottom = v_vertex.x + OUT.x * (ybottom - v_vertex.y) / OUT.y;" +
                        " float zbottom = v_vertex.z + OUT.z * (ybottom - v_vertex.y) / OUT.y;" +
                        " vec2 texCoord = vec2(xbottom, zbottom);" +
                        " vec4 waterColor = texture2D(u_texture0, texCoord);" +
                        " vec4 backgroundColor = texture2D(u_backgroundTexture, texCoord);"+  // Получите цвет фона из текстуры фона
                        " gl_FragColor = mix(backgroundColor, waterColor, waterColor.a * 0.75) * lightColor;"+  // Используйте mix для объединения фона и воды с учетом альфа-канала воды
                        "}";
        m_shader = new MyShader(vertex_shader, fragment_shader);
        m_shader.link_vertex_buffer(vertexes_buffer);
        m_shader.link_normal_buffer(normales_buffer);
        m_shader.link_texture(texture);
        m_shader.link_background_texture(backgroundTexture);  // Свяжите текстуру фона с шейдером
    }

    private int loadBackgroundTexture(int resourceId) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        InputStream stream;
        Bitmap bitmap;
        stream = c.getResources().openRawResource(resourceId);
        bitmap = BitmapFactory.decodeStream(stream);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        return textures[0];
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        float k = 0.055f;
        float left = -k * ratio;
        float right = k * ratio;
        float bottom = -k;
        float top = k;
        float near = 0.1f;
        float far = 10.0f;
        Matrix.frustumM(projection_matrix, 0, left, right, bottom, top, near, far);
        Matrix.multiplyMM(model_view_projection_matrix, 0, projection_matrix, 0, model_view_matrix, 0);
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        m_shader.link_model_view_projection_matrix(model_view_projection_matrix);
        m_shader.link_camera(x_camera, y_camera, z_camera);
        m_shader.link_light_source(x_light_position, y_light_position, z_light_position);
        get_vertexes();
        get_normales();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, size_index, GLES20.GL_UNSIGNED_SHORT,
                index_buffer);
    }
}