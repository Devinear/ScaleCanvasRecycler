package com.example.customcanvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class CanvasScrollActivity : AppCompatActivity(), OnScaleChangedListener, OnViewChangedListener, View.OnTouchListener {

    lateinit var scrollView : CanvasScrollView
    lateinit var canvasView : CanvasView // CanvasView // CanvasImageView

    lateinit var btAdd : Button
    lateinit var btUp : Button
    lateinit var btDown : Button

    lateinit var tvFirst : TextView
    lateinit var tvSecond: TextView
    lateinit var tvThird : TextView
    lateinit var tvForth : TextView
    lateinit var tvFifth : TextView

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

        scrollView.setOnScrollChangeListener(canvasView)

        btAdd = findViewById(R.id.btn_add)
        btAdd.setOnClickListener { clickAddBitmap() }
        btUp = findViewById(R.id.btn_up)
        btUp.setOnClickListener { clickUpScroll() }
        btDown = findViewById(R.id.btn_down)
        btDown.setOnClickListener { clickDownScroll() }

        tvFirst = findViewById(R.id.tv_first)
        tvSecond= findViewById(R.id.tv_second)
        tvThird = findViewById(R.id.tv_third)
        tvForth = findViewById(R.id.tv_forth)
        tvFifth = findViewById(R.id.tv_fifth)

        gestureDetector = GestureDetector(applicationContext, GestureListener())
        scaleGestureDetector = ScaleGestureDetector(applicationContext, ScaleGestureListener(this))

        for(i in 0..1) {
            clickAddBitmap()
        }
        scrollView.setOnTouchListener(this)
        touchSlop = ViewConfiguration.get(applicationContext).scaledTouchSlop.toFloat()
    }

    private fun clickAddBitmap() {
        Log.d(TAG, "clickAddBitmap")

        tempBitmap = Bitmap.createBitmap(900, 1000, Bitmap.Config.ARGB_8888)
        if(tempBitmap != null) {
            val canvas = Canvas(tempBitmap!!)
            canvas.drawColor(Color.RED)
        }
        val info = BitmapInfo(4, 5, 4, 1071, 1547)
        canvasView.addBitmap(tempBitmap!!, info)

//      canvasView.invalidate()    // invalidate는 size가 변경되지 않는다.
        canvasView.requestLayout() // onMeasure를 호출하므로 size가 변경된다.
        scrollView.smoothScrollTo(0, canvasView.height)
    }

    private fun clickUpScroll() {
        Log.d(TAG, "clickUpScroll")
        scrollView.smoothScrollTo(0, 0)
        canvasView.clickUp()
    }

    private fun clickDownScroll() {
        Log.d(TAG, "clickDownScroll")
        scrollView.smoothScrollTo(0, canvasView.height)
    }

    private var isDragging = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var touchSlop = 0f

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(event == null) return false
        Log.d(TAG, "onTouch Action:${event?.action}")
        val ret = scaleGestureDetector.onTouchEvent(event)

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                val dx = x - lastTouchX
                val dy = y - lastTouchY

                if(!isDragging && !canvasView.scaling) {
                    isDragging = sqrt((dx*dx)+(dy*dy)) >= touchSlop
                    if(isDragging) {
                        canvasView.onDragStart(x, y)
                    }
                }
                if((dx > 0 && canvasView.canDragStart) || (dx < 0 && canvasView.canDragEnd)) {
                    if(isDragging) {
                        canvasView.onDrag(dx, dy, x, y)
                        lastTouchX = x
                        lastTouchY = y
                    }
                }
                else {
                    isDragging = false
                }
            }
            MotionEvent.ACTION_UP -> {
                if(isDragging)
                    canvasView.onDragEnd()
                isDragging = false
            }
            MotionEvent.ACTION_CANCEL -> {
                if(isDragging)
                    canvasView.onDragEnd()
                isDragging = false
            }
        }
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
        canvasView.onScaleStart()
        return true
    }

    override fun onScaleEnd() {
        Log.d(TAG, "onScaleEnd")
        canvasView.onScaleEnd()
    }

    override fun onViewSize(width: Int, height: Int, scale: Float) {
        Log.d(TAG, "onScaleEnd Width:$width Height:$height Scale:$scale")
        tvFirst.text  = "Width :$width"
        tvSecond.text = "Height:$height"
        tvThird.text  = "Scale :$scale"
    }

    override fun onFindItem(first: Int, last: Int) {
        Log.d(TAG, "onFindItem first:$first last:$last")
        tvForth.text = "First:$first Last:$last"
    }

    override fun onPivotPoint(pivotX: Float, pivotY: Float) {
        Log.d(TAG, "onPivotPoint PivotX:$pivotX PivotY:$pivotY")
        tvFifth.text = "PivotX:$pivotX PivotY:$pivotY"
    }

    companion object {
        const val TAG = "[DE][AC] Activity"
    }
}