package com.example.customcanvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CanvasScrollActivity : AppCompatActivity() {

    lateinit var canvasView : CanvasView
    lateinit var btn : Button
    private var tempBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas_scoll)

        canvasView = findViewById(R.id.canvasView)
        btn = findViewById(R.id.btn)
        btn.setOnClickListener { clickBtn() }

//        val bitmap : Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        canvas.drawColor(Color.WHITE)
    }

    private fun clickBtn() {
        Log.d(TAG, "clickBtn")

        tempBitmap = Bitmap.createBitmap(500, 1000, Bitmap.Config.ARGB_8888)
        if(tempBitmap != null) {
            val canvas = Canvas(tempBitmap!!)
            canvas.drawColor(Color.RED)

//            val paint = Paint()
//            paint.color = applicationContext.getColor(R.color.colorPrimaryDark)
//            canvas.drawText("A", 10f, 10f, paint)
        }
        canvasView.addBitmap(tempBitmap!!)
        canvasView.requestLayout()
    }


    companion object {
        const val TAG = "[DE][AC] Activity"
    }
}