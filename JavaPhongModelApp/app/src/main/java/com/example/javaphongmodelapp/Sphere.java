package com.example.javaphongmodelapp;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class Sphere {
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int numIndices;
    private int program;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec3 aNormal;" +
                    "uniform mat4 uMVPMatrix;" +
                    "uniform vec3 uLightPosition;" +
                    "varying vec3 vNormal;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  vNormal = normalize(mat3(uMVPMatrix) * aNormal);" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec3 uAmbientColor;" +
                    "uniform vec3 uDiffuseColor;" +
                    "uniform vec3 uSpecularColor;" +
                    "uniform float uShininess;" +
                    "uniform vec3 uLightPosition;" +
                    "uniform vec3 uObjectColor;" +
                    "varying vec3 vNormal;" +
                    "void main() {" +
                    "  vec3 normal = normalize(vNormal);" +
                    "  vec3 lightDirection = normalize(uLightPosition - gl_FragCoord.xyz);" +
                    "  float ambient = max(dot(normal, lightDirection), 0.0);" +
                    "  vec3 ambientColor = uAmbientColor * ambient;" +
                    "  float diffuse = max(dot(normal, lightDirection), 0.0);" +
                    "  vec3 diffuseColor = uDiffuseColor * diffuse;" +
                    "  vec3 reflectionDirection = reflect(-lightDirection, normal);" +
                    "  vec3 viewDirection = normalize(-gl_FragCoord.xyz);" +
                    "  float specular = pow(max(dot(reflectionDirection, viewDirection), 0.0), uShininess);" +
                    "  vec3 specularColor = uSpecularColor * specular;" +
                    "  float ao = ambient * 0.3 + 0.1;"+
                    "  vec3 finalColor = uObjectColor * (ambientColor + diffuseColor + specularColor) * ao;" +
                    "  gl_FragColor = vec4(finalColor, 1.0);" +
                    "}";

    private int uLightPositionHandle;
    private int uAmbientColorHandle;
    private int uDiffuseColorHandle;
    private int uSpecularColorHandle;
    private int uShininessHandle;
    private int aNormalHandle;
    private int uObjectColorHandle;

    public Sphere(int numSegments, float radius) {
        // Компиляция шейдеров и создание программы
        int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        // Получение местоположения uniform-переменных и атрибутов
        uLightPositionHandle = GLES20.glGetUniformLocation(program, "uLightPosition");
        uAmbientColorHandle = GLES20.glGetUniformLocation(program, "uAmbientColor");
        uDiffuseColorHandle = GLES20.glGetUniformLocation(program, "uDiffuseColor");
        uSpecularColorHandle = GLES20.glGetUniformLocation(program, "uSpecularColor");
        uShininessHandle = GLES20.glGetUniformLocation(program, "uShininess");
        aNormalHandle = GLES20.glGetAttribLocation(program, "aNormal");

        // Получение местоположения uniform-переменной для цвета объекта
        uObjectColorHandle = GLES20.glGetUniformLocation(program, "uObjectColor");

        generateSphereVertices(numSegments, radius);
        generateNormals(numSegments);
    }

    private void generateSphereVertices(int numSegments, float radius) {
        List<Float> vertices = new ArrayList<>();
        List<Short> indices = new ArrayList<>();

        for (int i = 0; i <= numSegments; i++) {
            double theta = (double) i / numSegments * Math.PI;
            for (int j = 0; j <= numSegments; j++) {
                double phi = (double) j / numSegments * 2 * Math.PI;

                float x = (float) (radius * Math.sin(theta) * Math.cos(phi));
                float y = (float) (radius * Math.sin(theta) * Math.sin(phi));
                float z = (float) (radius * Math.cos(theta));

                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
            }
        }

        for (int i = 0; i < numSegments; i++) {
            for (int j = 0; j < numSegments; j++) {
                short topLeft = (short) (i * (numSegments + 1) + j);
                short topRight = (short) (i * (numSegments + 1) + j + 1);
                short bottomLeft = (short) ((i + 1) * (numSegments + 1) + j);
                short bottomRight = (short) ((i + 1) * (numSegments + 1) + j + 1);

                indices.add(topLeft);
                indices.add(bottomLeft);
                indices.add(topRight);

                indices.add(topRight);
                indices.add(bottomLeft);
                indices.add(bottomRight);
            }
        }

        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }

        short[] indexArray = new short[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indexArray[i] = indices.get(i);
        }

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertexArray.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.asFloatBuffer();
        vertexBuffer.put(vertexArray);
        vertexBuffer.position(0);

        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(indexArray.length * 2);
        indexByteBuffer.order(ByteOrder.nativeOrder());
        drawListBuffer = indexByteBuffer.asShortBuffer();
        drawListBuffer.put(indexArray);
        drawListBuffer.position(0);

        numIndices = indices.size();
    }

    private FloatBuffer normalBuffer;

    private void generateNormals(int numSegments) {
        // Генерация нормалей для каждой вершины сферы
        List<Float> normals = new ArrayList<>();
        for (int i = 0; i <= numSegments; i++) {
            double theta = (double) i / numSegments * Math.PI;
            for (int j = 0; j <= numSegments; j++) {
                double phi = (double) j / numSegments * 2 * Math.PI;

                float x = (float) (Math.sin(theta) * Math.cos(phi));
                float y = (float) (Math.sin(theta) * Math.sin(phi));
                float z = (float) (Math.cos(theta));

                normals.add(x);
                normals.add(y);
                normals.add(z);
            }
        }

        float[] normalArray = new float[normals.size()];
        for (int i = 0; i < normals.size(); i++) {
            normalArray[i] = normals.get(i);
        }

        ByteBuffer normalByteBuffer = ByteBuffer.allocateDirect(normalArray.length * 4);
        normalByteBuffer.order(ByteOrder.nativeOrder());
        normalBuffer = normalByteBuffer.asFloatBuffer();
        normalBuffer.put(normalArray);
        normalBuffer.position(0);
    }

    public void draw(float[] mvpMatrix, float[] lightPosition, float[] ambientColor, float[] diffuseColor, float[] specularColor, float shininess, float[] objectColor) {
        GLES20.glUseProgram(program);

        // Передача параметров освещения в шейдер
        GLES20.glUniform3fv(uLightPositionHandle, 1, lightPosition, 0);
        GLES20.glUniform3fv(uAmbientColorHandle, 1, ambientColor, 0);
        GLES20.glUniform3fv(uDiffuseColorHandle, 1, diffuseColor, 0);
        GLES20.glUniform3fv(uSpecularColorHandle, 1, specularColor, 0);
        GLES20.glUniform1f(uShininessHandle, shininess);

        // Установка цвета объекта
        GLES20.glUniform3fv(uObjectColorHandle, 1, objectColor, 0);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        // Включение атрибута для нормалей
        GLES20.glEnableVertexAttribArray(aNormalHandle);
        GLES20.glVertexAttribPointer(aNormalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(aNormalHandle);
    }

}
