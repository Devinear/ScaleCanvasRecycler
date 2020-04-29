package com.example.customcanvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class CanvasView : View, OnScaleChangedListener, OnDragChangedListener, View.OnScrollChangeListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    private val listBitmap = mutableListOf<Bitmap>()
    private val listInfo = mutableListOf<BitmapInfo>()

    private var canvasWidth  : Int = 0
    private var canvasHeight : Int = 0
    private var scaleWidth  : Int = 0
    private var scaleHeight : Int = 0
    private val displaySize = Point()

    private var isScaling = false
    val scaling : Boolean;  get() = isScaling

    private var isDragging = false
    val dragging : Boolean; get() = isDragging

    private var canDraggingStart = true
    private var canDraggingEnd = true
    val canDragStart : Boolean; get() = canDraggingStart
    val canDragEnd   : Boolean; get() = canDraggingEnd

    private var scaleFactor = 1f
    private var ratioX = 0f
    private var ratioY = 0f

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
        Log.d(TAG, "onDraw count:${listBitmap.size} Scale:$isScaling")
        if(canvas == null) return

        val paint = Paint()
        paint.color = context.getColor(R.color.colorPrimaryDark)
        paint.textSize = 50f

        val matrix : Matrix = this.matrix
        matrix.set(scaleMatrix)

        canvas.save()
        canvas.setMatrix(matrix)

        var index = 1
        var sumHeight = 0
        listBitmap.indices.forEach {
            val bitmap = listBitmap[it]
            val info = listInfo[it]

            val src = Rect(0, 0, bitmap.width, bitmap.height)
//            val left = getStartPosition(bitmap.width)
            val dst = Rect(info.marginStart, sumHeight, info.marginStart + info.width, sumHeight + info.height)
            canvas.drawBitmap(bitmap, src, dst, null)
            canvas.drawText("Page:$index / ${dst.top}", 10f, (dst.top + 50f), paint)

            index += 1
            sumHeight += info.posTop
            sumHeight += info.height

        }
        /*
        listBitmap.forEach {
            val src = Rect(0, 0, it.width, it.height)
            val left = getStartPosition(it.width)
//            val dst = Rect(left, sumHeight, left + it.width, sumHeight + it.height)
            val dst = Rect(left, sumHeight, left + it.width, sumHeight + it.height)
//            Log.d(TAG, "onDraw Page:$index Src:$src Dst:$dst")
            canvas.drawBitmap(it, src, dst, null)
            canvas.drawText("Page:$index", 10f, (dst.top + 50f), paint)

            index += 1
            sumHeight += 10
            sumHeight += it.height
        }
        */
        canvasHeight = sumHeight

        if(scaleHeight != (canvasHeight*scaleFactor).toInt()) {
            scaleWidth  = (displaySize.x*scaleFactor).toInt()
            scaleHeight = (canvasHeight*scaleFactor).toInt()
            requestLayout()
        }

        canvas.restore()
        listener?.onViewSize(width = scaleWidth, height = scaleHeight, scale = scaleFactor)
    }

    // 음수가 나올 수 있음.
    private fun getStartPosition(width: Int) : Int = (displaySize.x-width)/2

    fun addBitmap(bitmap: Bitmap, info: BitmapInfo) {
        Log.d(TAG, "addBitmap width:${bitmap.width} height:${bitmap.height}")
        listBitmap.add(bitmap)
        listInfo.add(info)

        canvasHeight += info.posTop
        canvasHeight += info.height
        scaleHeight = canvasHeight

        // 초기...?
        canvasWidth = displaySize.x
        scaleWidth = canvasWidth
    }

    fun updateBitmapInfo(index:Int, info: BitmapInfo) : Boolean {
        Log.d(TAG, "updateBitmapInfo index:$index info:$info")
        if(listInfo.size <= index) return false
        listInfo[index] = info
        return true
    }

    fun clickUp() {
        Log.d(TAG, "clickUp")
        initScale()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure width:$widthMeasureSpec/$width/$canvasWidth height:$heightMeasureSpec/$height/$canvasHeight/$scaleHeight")
        setMeasuredDimension(widthMeasureSpec, scaleHeight)
    }

    private fun initScale() {
        Log.d(TAG, "initScale")
        scaleFactor = 1f
        scaleMatrix = Matrix()
    }

    private var initFocusX = -1f
    private var initFocusY = -1f

    override fun onScaleChange(scaleFactor: Float, focusX: Float, focusY: Float): Boolean {
        Log.d(TAG, "onScaleChange ScaleFactor:$scaleFactor/${this.scaleFactor} FocusX:$focusX FocusY:$focusY")
        if((this.scaleFactor == minScale && scaleFactor < 1) || (this.scaleFactor == maxScale && scaleFactor > 1))
            return false

        val rectGlobal = Rect()
        getGlobalVisibleRect(rectGlobal)

        val rectDrawing = Rect()
        getDrawingRect(rectDrawing)

        val rectFocused = Rect()
        getFocusedRect(rectFocused)

        val rectHit = Rect()
        getHitRect(rectHit)

        val rectLocal = Rect()
        getLocalVisibleRect(rectLocal)

        val rectMap = RectF()
        scaleMatrix.mapRect(rectMap)

        if(!isScaling) {
            isScaling = true
            initFocusX = focusX
            initFocusY = focusY

            ratioX = (focusX + rectMap.left - rectGlobal.left) / scaleWidth
            ratioY = (focusY + curScrollTop - rectGlobal.top) / scaleHeight
        }

        this.scaleFactor *= scaleFactor
        if(this.scaleFactor < minScale)
            this.scaleFactor = minScale
        else if(this.scaleFactor > maxScale)
            this.scaleFactor = maxScale

        val baseMatrix = Matrix()


        val newX = ratioX * scaleWidth + rectGlobal.left - rectMap.left
        val newY = ratioY * scaleHeight + rectGlobal.top - curScrollTop
        Log.d(TAG, "onScaleChange FocusX:${focusX.toInt()} newX:${newX.toInt()} FocusY:${focusY.toInt()} newY:${newY.toInt()}")

//        val matrix = this.matrix
//        matrix.postScale(this.scaleFactor, this.scaleFactor, focusX, focusY)
//        scaleMatrix.set(matrix)

//        scaleMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
        scaleMatrix.postScale(scaleFactor, scaleFactor, initFocusX, initFocusY)

        invalidate()
//        requestLayout()
        return true
    }

    private fun getFocusByRatioX(focusX: Float) : Float {
        var ratioX = 0f

        scaleWidth

        return ratioX
    }

    override fun onScaleStart(): Boolean {
        Log.d(TAG, "onScaleStart")
//        isScaling = true
//        this.focusX = -1f
//        this.focusY = -1f
        return true
    }

    override fun onScaleEnd() {
        Log.d(TAG, "onScaleEnd")
        if(scaleFactor < 1) {
            initScale()
            invalidate() // Scale 조정하는 것이므로 화면 갱신 필요
        }
        else {
            val rectF = RectF()
            scaleMatrix.mapRect(rectF)

            if(rectF.top < 0)
                scaleMatrix.postTranslate(0f, abs(rectF.top))
        }
        this.ratioX = -1f
        this.ratioY = -1f
        this.initFocusX = -1f
        this.initFocusY = -1f
        android.os.Handler().postDelayed({ isScaling = false }, DELAY_TIME)
    }

    override fun onDrag(dx: Float, dy: Float, focusX: Float, focusY: Float) {
        Log.d(TAG, "onDrag dx:$dx dy:$dy focusX:$focusX focusY:$focusY")
        val marginStart = computeCanDraggingStart(focusY = focusY)
        if(dx > 0 && !canDraggingStart)
            return
        val marginEnd = computeCanDraggingEnd(focusY = focusY)
        if(dx < 0 && !canDraggingEnd)
            return

        var newDx = dx
        if(newDx > 0 && newDx > abs(marginStart)) {
            newDx = abs(marginStart).toFloat()
        }
        else if(newDx < 0 && abs(newDx) > marginEnd) {
            newDx = -(marginEnd.toFloat())
        }

        scaleMatrix.postTranslate(newDx, 0f)
        invalidate()
    }

    override fun onDragStart(focusX: Float, focusY: Float) {
        Log.d(TAG, "onDragStart focusX:$focusX focusY:$focusY")

        computeCanDraggingStart(focusY = focusY)
        computeCanDraggingEnd(focusY = focusY)
    }

    private fun computeCanDraggingStart(focusY: Float) : Float {
        val rectF = RectF()
        scaleMatrix.mapRect(rectF)
        canDraggingStart = rectF.left < 0
        return rectF.left
    }

    private fun computeCanDraggingEnd(focusY: Float) : Float {
        val rectF = RectF()
        scaleMatrix.mapRect(rectF)

        val position = findTouchItemPosition(focusY.toInt())
        val info = listInfo[position]

        var posEnd = (info.marginStart+info.width+info.marginEnd)*scaleFactor
        posEnd += rectF.left

        canDraggingEnd = (posEnd > displaySize.x)
        return posEnd-displaySize.x
    }

    override fun onDragEnd() {
        Log.d(TAG, "onDragEnd")
    }

    private var curScrollTop: Int = 0

    override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        Log.d(TAG, "onScrollChange ScrollX:[$oldScrollX] > [$scrollX] ScrollY:[$oldScrollY] > [$scrollY]")
        curScrollTop = scrollY

        val first = findFirstVisibleItemPosition()
        val last  = findLastVisibleItemPosition()
        listener?.onFindItem(first, last)
    }

    private fun findTouchItemPosition(focusY: Int): Int {
        val rect = Rect()
        getGlobalVisibleRect(rect)

        val top = curScrollTop
        var touchY = top + (focusY - rect.top)

        listInfo.indices.forEach {
            val info = listInfo[it]
            touchY -= (info.posTop + info.height)
            if(touchY <= 0)
                return it
        }
        return listInfo.lastIndex
    }

    private fun findFirstVisibleItemPosition(): Int {
        val rect = Rect()
        getGlobalVisibleRect(rect)

        var top = curScrollTop

        listInfo.indices.forEach {
            val info = listInfo[it]
            top -= (info.posTop + info.height)
            if(top <= 0)
                return it
        }
        return listInfo.lastIndex
    }

    private fun findLastVisibleItemPosition(): Int {
        val rect = Rect()
        getGlobalVisibleRect(rect)

        val top = curScrollTop
        var bottom = top+rect.height()

        listInfo.indices.forEach {
            val info = listInfo[it]
            bottom -= (info.posTop + info.height)
            if(bottom <= 0)
                return it
        }
        return listInfo.lastIndex
    }

    companion object {
        const val TAG = "[DE][VI] Canvas"
        const val DELAY_TIME = 200L
    }
}