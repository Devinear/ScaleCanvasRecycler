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

    private var scaleMatrix = Matrix()
    val minScale = 0.8f
    val maxScale = 2.0f

    var listener : OnViewChangedListener? = null


    init {
        (context as AppCompatActivity).windowManager.defaultDisplay.getSize(displaySize)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        Log.d(TAG, "onDraw count:${list.size} Scale:$isScaling")
        if(canvas == null) return

        val paint = Paint()
        paint.color = context.getColor(R.color.colorPrimaryDark)
        paint.textSize = 50f

        var index = 1
        var sumHeight = 0

        val matrix = this.matrix
        matrix.setScale(scaleFactor, scaleFactor, focusX, focusY)

        canvas.save()
        canvas.setMatrix(matrix)

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
        scaleHeight = (canvasHeight*scaleFactor).toInt()

        canvas.restore()
        listener?.onViewSize(width = scaleWidth, height = scaleHeight, scale = scaleFactor)
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

    fun clickUp() {
        Log.d(TAG, "clickUp")
        initScale()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure width:$widthMeasureSpec/$width/$canvasWidth height:$heightMeasureSpec/$height/$canvasHeight")
//        setMeasuredDimension(widthMeasureSpec, canvasHeight)
        setMeasuredDimension(scaleWidth, scaleHeight)
    }

    private fun initScale() {
        Log.d(TAG, "initScale")
        scaleFactor = 1f
        scaleMatrix = Matrix()
    }

    override fun onScaleChange(scaleFactor: Float, focusX: Float, focusY: Float): Boolean {
        Log.d(TAG, "onScaleChange ScaleFactor:$scaleFactor/${this.scaleFactor} FocusX:$focusX FocusY:$focusY")
        if((this.scaleFactor == minScale && scaleFactor < 1) || (this.scaleFactor == maxScale && scaleFactor > 1))
            return false

        isScaling = true

        this.scaleFactor *= scaleFactor
        if(this.scaleFactor < minScale)
            this.scaleFactor = minScale
        else if(this.scaleFactor > maxScale)
            this.scaleFactor = maxScale

        this.focusX = focusX
        this.focusY = focusY

        val matrix = this.matrix
        matrix.postScale(this.scaleFactor, this.scaleFactor, focusX, focusY)

        scaleMatrix = matrix

        invalidate()
//        requestLayout()
        return true
    }

    override fun onScaleStart(): Boolean {
        Log.d(TAG, "onScaleStart")
        isScaling = true
        return true
    }

    override fun onScaleEnd() {
        Log.d(TAG, "onScaleEnd")
        isScaling = false

        if(scaleFactor < 1) {
            scaleFactor = 1f
        }
    }

    companion object {
        const val TAG = "[DE][VI] Canvas"
    }
}