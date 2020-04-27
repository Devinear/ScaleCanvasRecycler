package com.example.customcanvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CanvasScrollActivity : AppCompatActivity(), OnScaleChangedListener, OnViewChangedListener, View.OnTouchListener {

    lateinit var scrollView : ScrollView
    lateinit var canvasView : CanvasView

    lateinit var btAdd : Button
    lateinit var btUp : Button
    lateinit var btDown : Button

    lateinit var tvFirst : TextView
    lateinit var tvSecond: TextView
    lateinit var tvThird : TextView

    private var tempBitmap: Bitmap? = null

    private lateinit var gestureDetector : GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas_scoll)

        scrollView = findViewById(R.id.scrollView)
        canvasView = findViewById(R.id.canvasView)
        canvasView.listener = this

        btAdd = findViewById(R.id.btn_add)
        btAdd.setOnClickListener { clickAddBitmap() }
        btUp = findViewById(R.id.btn_up)
        btUp.setOnClickListener { clickUpScroll() }
        btDown = findViewById(R.id.btn_down)
        btDown.setOnClickListener { clickDownScroll() }

        tvFirst = findViewById(R.id.tv_first)
        tvSecond= findViewById(R.id.tv_second)
        tvThird = findViewById(R.id.tv_third)

        gestureDetector = GestureDetector(applicationContext, GestureListener())
        scaleGestureDetector = ScaleGestureDetector(applicationContext, ScaleGestureListener(this))

        for(i in 0..10) {
            clickAddBitmap()
        }
        scrollView.setOnTouchListener(this)
    }

    private fun clickAddBitmap() {
        Log.d(TAG, "clickAddBitmap")

        tempBitmap = Bitmap.createBitmap(900, 1000, Bitmap.Config.ARGB_8888)
        if(tempBitmap != null) {
            val canvas = Canvas(tempBitmap!!)
            canvas.drawColor(Color.RED)
        }
        canvasView.addBitmap(tempBitmap!!)

//      canvasView.invalidate()    // invalidate는 size가 변경되지 않는다.
        canvasView.requestLayout() // onMeasure를 호출하므로 size가 변경된다.

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

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouch Action:${event?.action}")
        val ret = scaleGestureDetector.onTouchEvent(event)
        Log.d(TAG, "onTouch Return:$ret")
        return false
    }

    override fun onScaleChange(scaleFactor: Float, focusX: Float, focusY: Float): Boolean {
        Log.d(TAG, "onScaleChange ScaleFactor:$scaleFactor FocusX:$focusX FocusY:$focusY")
        canvasView.onScaleChange(scaleFactor, focusX, focusY)
        return true
    }

    override fun onScaleStart(): Boolean {
        Log.d(TAG, "onScaleStart")
        return true
    }

    override fun onScaleEnd() {
        Log.d(TAG, "onScaleEnd")
    }

    override fun onViewSize(width: Int, height: Int, scale: Float) {
        Log.d(TAG, "onScaleEnd Width:$width Height:$height Scale:$scale")
        tvFirst.text  = "Width :$width"
        tvSecond.text = "Height:$height"
        tvThird.text  = "Scale :$scale"
    }

    companion object {
        const val TAG = "[DE][AC] Activity"
    }
}