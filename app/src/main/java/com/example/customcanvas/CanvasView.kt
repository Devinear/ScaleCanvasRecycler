package com.example.customcanvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class CanvasView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    private val list = mutableListOf<Bitmap>()
    private var viewHeight : Int = 0
    private val displaySize = Point()

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
        var height = 0
        list.forEach {
            val src = Rect(0, 0, it.width, it.height)
            val left = getStartPosition(it.width)
            val dst = Rect(left, height, left+it.width, height+it.height)
            Log.d(TAG, "onDraw Page:$index Src:$src Dst:$dst")
            canvas.drawBitmap(it, src, dst, null)
            canvas.drawText("Page:$index", 10f, (dst.top + 50f), paint)

            index += 1
            height += 10
            height += it.height
        }
    }

    // 음수가 나올 수 있음.
    private fun getStartPosition(width: Int) : Int = (displaySize.x-width)/2

    fun addBitmap(bitmap: Bitmap) {
        Log.d(TAG, "addBitmap width:${bitmap.width} height:${bitmap.height}")
        list.add(bitmap)
        viewHeight += 10
        viewHeight += bitmap.height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure width:$widthMeasureSpec/$width height:$heightMeasureSpec/$height/$viewHeight")
        setMeasuredDimension(widthMeasureSpec, viewHeight)
    }

    companion object {
        const val TAG = "[DE][VI] Canvas"
    }
}