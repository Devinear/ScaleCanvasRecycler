package com.example.customcanvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity

class CanvasScrollActivity : AppCompatActivity() {

    lateinit var scrollView : ScrollView
    lateinit var canvasView : CanvasView

    lateinit var btAdd : Button
    lateinit var btUp : Button
    lateinit var btDown : Button

    private var tempBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas_scoll)

        scrollView = findViewById(R.id.scrollView)
        canvasView = findViewById(R.id.canvasView)
        btAdd = findViewById(R.id.btn_add)
        btAdd.setOnClickListener { clickAddBitmap() }
        btUp = findViewById(R.id.btn_up)
        btUp.setOnClickListener { clickUpScroll() }
        btDown = findViewById(R.id.btn_down)
        btDown.setOnClickListener { clickDownScroll() }

//        val bitmap : Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        canvas.drawColor(Color.WHITE)
    }

    private fun clickAddBitmap() {
        Log.d(TAG, "clickAddBitmap")

        tempBitmap = Bitmap.createBitmap(900, 1000, Bitmap.Config.ARGB_8888)
        if(tempBitmap != null) {
            val canvas = Canvas(tempBitmap!!)
            canvas.drawColor(Color.RED)
        }
        canvasView.addBitmap(tempBitmap!!)
//        canvasView.invalidate()
        canvasView.requestLayout()

        scrollView.smoothScrollTo(0, canvasView.height)
    }

    private fun clickUpScroll() {
        Log.d(TAG, "clickUpScroll")
        scrollView.smoothScrollTo(0, 0)
    }

    private fun clickDownScroll() {
        Log.d(TAG, "clickDownScroll")
        scrollView.smoothScrollTo(0, canvasView.height)
    }

    companion object {
        const val TAG = "[DE][AC] Activity"
    }
}