package com.example.customcanvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class CanvasView : View, OnScaleChangedListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    private val list = mutableListOf<Bitmap>()
    private var canvasWidth  : Int = 0
    private var canvasHeight : Int = 0
    private var scaleWidth  : Int = 0
    private var scaleHeight : Int = 0
    private val displaySize = Point()

    private var isScaling = false

    private var scaleFactor = 1f
    private var focusX = 0f
    private var focusY = 0f

    var listener : OnViewChangedListener? = null


    init {
        (context as AppCompatActivity).windowManager.defaultDisplay.getSize(displaySize)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        Log.d(TAG, "onDraw count:${list.size}")
        if(canvas == null) return

        val paint = Paint()
        paint.color = context.getColor(R.color.colorPrimaryDark)
        paint.textSize = 50f

        var index = 1
        var sumHeight = 0

        canvas.save()
        if(isScaling) {
            canvas.scale(scaleFactor, scaleFactor, focusX, focusY)
        }

        list.forEach {
            val src = Rect(0, 0, it.width, it.height)
            val left = getStartPosition(it.width)
            val dst = Rect(left, sumHeight, left + it.width, sumHeight + it.height)
//            Log.d(TAG, "onDraw Page:$index Src:$src Dst:$dst")
            canvas.drawBitmap(it, src, dst, null)
            canvas.drawText("Page:$index", 10f, (dst.top + 50f), paint)

            index += 1
            sumHeight += 10
            sumHeight += it.height
        }
        canvasHeight = sumHeight
//        canvasWidth = canvas.width
//        canvasHeight = canvas.height

        if(isScaling) {
//            scaleWidth = (canvasWidth*scaleFactor).toInt()
            scaleHeight = (canvasHeight*scaleFactor).toInt()
        }

        canvas.restore()

//        setMeasuredDimension(width, viewHeight)

        listener?.onViewSize(width = scaleWidth, height = scaleHeight)
    }

    // 음수가 나올 수 있음.
    private fun getStartPosition(width: Int) : Int = (displaySize.x-width)/2

    fun addBitmap(bitmap: Bitmap) {
        Log.d(TAG, "addBitmap width:${bitmap.width} height:${bitmap.height}")
        list.add(bitmap)
        canvasWidth = displaySize.x
        canvasHeight += 10
        canvasHeight += bitmap.height

        scaleWidth = canvasWidth
        scaleHeight = canvasHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure width:$widthMeasureSpec/$width/$canvasWidth height:$heightMeasureSpec/$height/$canvasHeight")
//        setMeasuredDimension(widthMeasureSpec, canvasHeight)
        setMeasuredDimension(scaleWidth, scaleHeight)
    }

    override fun onScaleChange(scaleFactor: Float, focusX: Float, focusY: Float): Boolean {
        Log.d(TAG, "onScaleChange ScaleFactor:$scaleFactor/${this.scaleFactor} FocusX:$focusX FocusY:$focusY")
        if((this.scaleFactor == 0.8f && scaleFactor < 1) || (this.scaleFactor == 2f && scaleFactor > 1))
            return false

//        scaleX = scaleFactor
//        scaleY = scaleFactor
        isScaling = true

//        val matrix = Matrix()
//        matrix.postScale(scaleFactor, scaleFactor, focusX, focusX)

        this.scaleFactor *= scaleFactor
        if(this.scaleFactor < 0.8f)
            this.scaleFactor = 0.8f
        else if(this.scaleFactor > 2f)
            this.scaleFactor = 2f

        this.focusX = focusX
        this.focusY = focusY

//        invalidate()
        requestLayout()
        return true
    }

    override fun onScaleStart(): Boolean {
        Log.d(TAG, "onScaleStart")
        isScaling = true
        scaleFactor = 1f
        return true
    }

    override fun onScaleEnd() {
        Log.d(TAG, "onScaleEnd")
        isScaling = false
        scaleFactor = 1f
    }

    companion object {
        const val TAG = "[DE][VI] Canvas"
    }
}