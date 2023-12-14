package com.example.kotlincourseproject

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import android.view.View
import com.example.kotlincourseproject.gl.GLObject
import com.example.kotlincourseproject.gl.IGLObject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


@SuppressLint("ClickableViewAccessibility")
class MyRenderer(ctx: Context, private val glSurfaceView: GLSurfaceView) : GLSurfaceView.Renderer,
    View.OnTouchListener {
    private val candleObjects: MutableList<IGLObject> = mutableListOf()
    private val otherObjects: MutableList<IGLObject> = mutableListOf()

    private var currentCandleIndex = 0 // Индекс текущей свечи
    private var framesPerCandle = 5 // Количество кадров для каждой свечи
    private var framesCount = 0 // Счетчик кадров

    private val bgColorR: Float = 0.3f
    private val bgColorG: Float = 0.3f
    private val bgColorB: Float = 0.3f
    private val bgColorA: Float = 1.0f


    init {
        val baseShader = Utils.inputStreamToString(ctx.assets.open("shaders/base_shader.vert"))
        val colorShader = Utils.inputStreamToString(ctx.assets.open("shaders/color_shader.frag"))
        val textureShader =
            Utils.inputStreamToString(ctx.assets.open("shaders/texture_shader.frag"))

        val table = GLObject.fromInputStream(
            baseShader,
            textureShader,
            ctx.assets.open("models/wooden_table.obj"),
            ctx.assets.open("models/wooden_table.jpeg")
        )
        table.setPosition(0f, -3.2f, -11f)
        table.setRotation(0f, 30f, 0f)
        table.setScale(0.1f, 0.1f, 0.1f)

        for (i in 0..20) {
            val candleModelNumber = i % 21
            val candleFileName = "models/candles/candle$candleModelNumber.obj"
            val textureFileName = "models/candles/textures/candles_albedo.jpg"

            val candle = GLObject.fromInputStream(
                baseShader,
                textureShader,
                ctx.assets.open(candleFileName),
                ctx.assets.open(textureFileName)
            )

            candle.setPosition(table.x + 1.2f, table.y + 1.45f, table.z + 3f)
            candle.setRotation(table.rotateX, table.rotateY, table.rotateZ)
            candle.setScale(0.6f, 0.6f, 0.6f)

            candleObjects.add(candle)
        }

        val mug = GLObject.fromInputStream(
            baseShader,
            textureShader,
            ctx.assets.open("models/mug.obj"),
            ctx.assets.open("models/mug.jpg")
        )
        mug.setPosition(table.x - 0.2f, table.y + 1.5f, table.z + 2f)
        mug.setRotation(table.rotateX, table.rotateY, table.rotateZ)
        mug.setScale(2.2f, 2.2f, 2.2f)

        val strawberry_cat = GLObject.fromInputStream(
            baseShader,
            textureShader,
            ctx.assets.open("models/strawberry_cat.obj"),
            ctx.assets.open("models/strawberry_cat.png")
        )
        strawberry_cat.setPosition(table.x + 0.4f, table.y + 1.4f, table.z + 2.5f)
        strawberry_cat.setRotation(table.rotateX, table.rotateY, table.rotateZ)
        strawberry_cat.setScale(0.15f, 0.15f, 0.15f)

        val banana_cat = GLObject.fromInputStream(
            baseShader,
            textureShader,
            ctx.assets.open("models/banana_cat.obj"),
            ctx.assets.open("models/banana_cat.png")
        )
        banana_cat.setPosition(table.x + 0.9f, table.y + 1.4f, table.z + 2.5f)
        banana_cat.setRotation(table.rotateX, table.rotateY, table.rotateZ)
        banana_cat.setScale(0.15f, 0.15f, 0.15f)

        val pumpkin = GLObject.fromInputStream(
            baseShader,
            textureShader,
            ctx.assets.open("models/pumpkin.obj"),
            ctx.assets.open("models/pumpkin.png")
        )
        pumpkin.setPosition(table.x, table.y + 1.8f, table.z - 2f)
        pumpkin.setRotation(table.rotateX, table.rotateY, table.rotateZ)
        pumpkin.setScale(1f, 1f, 1f)

        val abstract = GLObject.fromInputStream(
            baseShader,
            textureShader,
            ctx.assets.open("models/abstract.obj"),
            ctx.assets.open("models/abstract.png")
        )
        abstract.setPosition(table.x - 1.6f, table.y + 2.2f, table.z - 2f)
        abstract.setRotation(table.rotateX, table.rotateY, table.rotateZ)
        abstract.setScale(1f, 1f, 1f)

        otherObjects.add(table)
        otherObjects.add(mug)
        otherObjects.add(strawberry_cat)
        otherObjects.add(banana_cat)
        otherObjects.add(pumpkin)
        otherObjects.add(abstract)

        glSurfaceView.setOnTouchListener(this)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        for (obj in candleObjects + otherObjects) {
            obj.onSurfaceCreated(gl, config)
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        for (obj in candleObjects + otherObjects) {
            obj.onSurfaceChanged(gl, width, height)
        }
    }

//    override fun onDrawFrame(gl: GL10) {
//        glClearColor(bgColorR, bgColorG, bgColorB, bgColorA)
//        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
//
//        drawCandles(gl)
//        drawOtherObjects(gl)
//    }

    private fun drawCandles(gl: GL10) {
        val currentCandle = (candleObjects[currentCandleIndex] as GLObject)

        currentCandle.setLightDirection(0f, 1f, 0f)
        currentCandle.onDrawFrame(gl)

        framesCount++
        if (framesCount >= framesPerCandle) {
            framesCount = 0
            currentCandleIndex = (currentCandleIndex + 1) % candleObjects.size
        }
    }

    private fun drawOtherObjects(gl: GL10) {
        val currentCandle = (candleObjects[currentCandleIndex] as GLObject)

        for (obj in otherObjects) {
            obj.setLightDirection(currentCandle.x - obj.x, currentCandle.y + 1f - obj.y, currentCandle.z - obj.z)
            obj.onDrawFrame(gl)
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startRotation()
            }
            MotionEvent.ACTION_UP -> {
                stopRotation()
            }
        }
        return true
    }

    private var isRotating = false

    private fun startRotation() {
        isRotating = true
    }

    private fun stopRotation() {
        isRotating = false
        resetRotation()
    }

    private fun resetRotation() {
        for (obj in candleObjects + otherObjects) {
            //obj.setRotation(0f, 30f, 0f)
        }
    }

    override fun onDrawFrame(gl: GL10) {
        glClearColor(bgColorR, bgColorG, bgColorB, bgColorA)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if (isRotating) {
            for (obj in otherObjects) {
                if (obj !is GLObject || obj !== otherObjects[0]) {
                    obj.rotateY += 2.5f
                }
            }
        }

        drawCandles(gl)
        drawOtherObjects(gl)
    }
}

