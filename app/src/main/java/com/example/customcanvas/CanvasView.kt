package com.example.customcanvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class CanvasView : View, OnScaleChangedListener, OnDragChangedListener, View.OnScrollChangeListener, OnScrollChangedListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    val rectGlobal = Rect()
    var scrollView : CanvasScrollView? = null

    private val listBitmap = mutableListOf<Bitmap>()
    private val listInfo = mutableListOf<BitmapInfo>()

    private var canvasWidth  : Int = 0
    private var canvasHeight : Int = 0
    private var scaleWidth  : Int = 0
    private var scaleHeight : Int = 0

    private val displaySize = Point()
    var isPortrait: Boolean = true
    val displayWidth: Int
        get() = if(isPortrait) displaySize.x else displaySize.y
    val displayHeight: Int
        get() = if(isPortrait) displaySize.y else displaySize.x

    private var isScaling = false
    val scaling : Boolean;  get() = isScaling

    private var isDragging = false
    val dragging : Boolean; get() = isDragging

    private var canDraggingStart = true
    private var canDraggingEnd = true
    val canDragStart : Boolean; get() = canDraggingStart
    val canDragEnd   : Boolean; get() = canDraggingEnd

    private var scaleFactor = 1f
    private var scaleMatrix = Matrix()

    private var isZoomOut : Boolean = false
    private var isSetPosYByFitScale : Boolean = false
    private var posYByFitScale = 0

    val minScale = 0.8f
    val maxScale = 4.0f

    private var mode = ViewMode.Continuous

    private var positionX = 0
    private var positionY = 0

    var listener : OnViewChangedListener? = null


    init {
        (context as AppCompatActivity).windowManager.defaultDisplay.getSize(displaySize)
    }

    fun initListener() {
        scrollView?.scrollListener = this
    }

    fun changeViewMode(mode: ViewMode) {
        Log.d(TAG, "changeViewMode MODE:[${this.mode}]>>[$mode]")
        this.mode = mode
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        Log.d(TAG, "onDraw count:${listBitmap.size} Scale:$isScaling")
        if(canvas == null) return

        val paintText = Paint()
        paintText.color = Color.WHITE
        paintText.textSize = 50f

        val paintLine = Paint()
        paintLine.color = Color.WHITE

//        if(!isScaling) {
//            scaleMatrix = Matrix()
//        }

        val matrix : Matrix = this.matrix
        matrix.set(scaleMatrix)

        canvas.save()
        canvas.setMatrix(matrix)

        val rectLocal = Rect()
        getLocalVisibleRect(rectLocal)

        val rectMap = RectF()
        matrix.mapRect(rectMap)

        Log.d(TAG, "onDraw RECT LOCAL[$rectLocal] MAP[$rectMap]")

        var index = 1
        var sumHeight = 0

        listBitmap.indices.forEach {
            val bitmap = listBitmap[it]
            val info = listInfo[it]

            val src = Rect(0, 0, bitmap.width, bitmap.height)
            val dst = Rect(
                info.marginStart,
                sumHeight,
                info.marginStart + info.width,
                sumHeight + info.height
            )
            canvas.drawBitmap(bitmap, src, dst, null)
            canvas.drawText("Page:$index / ${dst.top}", 10f, (dst.top + 50f), paintText)

            index += 1
            sumHeight += info.posTop
            sumHeight += info.height

            val left   = dst.left.toFloat()
            val top    = dst.top.toFloat()
            val right  = dst.right.toFloat()
            val bottom = dst.bottom.toFloat()

            // startX, startY, stopX, stopY
            canvas.drawLine(left,   top,    right,  top,    paintLine)
            canvas.drawLine(left,   top,    left,   bottom, paintLine)
            canvas.drawLine(left,   bottom, right,  bottom, paintLine)
            canvas.drawLine(right,  top,    right,  bottom, paintLine)
        }
        canvasHeight = sumHeight

        if (canvasHeight < rectGlobal.height())
            canvasHeight = rectGlobal.height()

        if (scaleHeight != canvasHeight) {
            scaleHeight = canvasHeight
            requestLayout()
        }
        canvas.restore()

        positionX = rectLocal.left
        positionY = rectLocal.top
        if(isScaling) {
            positionX -= rectMap.left.toInt()
            positionY -= rectMap.top.toInt()
        }

        if(!isSetPosYByFitScale && isZoomOut && this.scaleFactor <= 1f) {
            isSetPosYByFitScale = true
            if(mode == ViewMode.Continuous)
                posYByFitScale = (positionY.toFloat() / this.scaleFactor).toInt()
            else
                posYByFitScale = 0 // OnePage
        }

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
        Log.d(TAG, "onMeasure WIDTH:$widthMeasureSpec/$width/$canvasWidth HEIGHT:$heightMeasureSpec/$height/$canvasHeight/$scaleHeight")
        setMeasuredDimension(widthMeasureSpec, scaleHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d(TAG, "onSizeChanged WIDTH:[$oldw]>>[$w] HEIGHT:[$oldh]>>[$h]")
        super.onSizeChanged(w, h, oldw, oldh)
        getGlobalVisibleRect(rectGlobal)
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

        isZoomOut = scaleFactor < 1f

        var scale = scaleFactor

        val rectLocal = Rect()
        getLocalVisibleRect(rectLocal)

        val rectMapBefore = RectF()
        scaleMatrix.mapRect(rectMapBefore)

        if(!isScaling) {
            isScaling = true
        }

        this.scaleFactor *= scale
        if(this.scaleFactor < minScale) {
            this.scaleFactor = minScale
            scale = minScale / this.scaleFactor
        }
        else if(this.scaleFactor > maxScale) {
            this.scaleFactor = maxScale
            scale = maxScale / this.scaleFactor
        }

        // 확대된 Canvas를 고려하여 Pivot Point 설정
        val x = focusX + rectLocal.left
        val y = focusY + rectLocal.top
        Log.d(TAG, "onScaleChange x:$x y:$y Local:$rectLocal")

        scaleMatrix.postScale(scale, scale, x, y)
        listener?.onPivotPoint(x, y)

        scaleWidth  = ((displayWidth) * (this.scaleFactor)).toInt()
        invalidate()
        return true
    }

    override fun onScaleStart(): Boolean {
        Log.d(TAG, "onScaleStart")
        isScaling = true
//        scaleMatrix = Matrix()

        if(this.scaleFactor == 1.0f) {
            posYByFitScale = positionY
            isSetPosYByFitScale = true
        }
        else {
            isSetPosYByFitScale = false
        }

        return true
    }

    override fun onScaleEnd() {
        val rectMap = RectF();  scaleMatrix.mapRect(rectMap)
        val rectLocal = Rect(); getLocalVisibleRect(rectLocal)

        if(scaleFactor < 1) {
            Log.d(TAG, "onScaleEnd Scale:$scaleFactor")
            initScale()
            positionY = posYByFitScale
        }
        else {
            val moveTop = abs(rectMap.top)
            if(rectMap.top < 0) {
                scaleMatrix.postTranslate(0f, moveTop)
                (context as CanvasScrollActivity).scrollView.scrollBy(0, moveTop.toInt())
            }

            val moveLeft = if(rectMap.left<0) (scaleWidth-abs(rectMap.left)) else 0f
            val moveRight = if(rectMap.left + scaleWidth < displaySize.x) (displaySize.x - (rectMap.left + scaleWidth)) else 0f
            if(moveRight > 0) {
                scaleMatrix.postTranslate(moveRight, 0f)
            }

            Log.d(TAG, "onScaleEnd Scale:$scaleFactor moveTop:$moveTop moveLeft:$moveLeft moveRight:$moveRight")
        }
        isScaling = false

        requestLayout()
        invalidate()
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

    override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
//        Log.d(TAG, "onScrollChange ScrollX:[$oldScrollX] > [$scrollX] ScrollY:[$oldScrollY] > [$scrollY]")
        val first = findFirstVisibleItemPosition()
        val last  = findLastVisibleItemPosition()
        listener?.onFindItem(first, last)
    }

    private fun findTouchItemPosition(focusY: Int): Int {
        val rect = Rect()
        getLocalVisibleRect(rect)
        var touchY = rect.top + focusY

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
        getLocalVisibleRect(rect)
        var top = rect.top

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
        getLocalVisibleRect(rect)
        var bottom = rect.bottom

        listInfo.indices.forEach {
            val info = listInfo[it]
            bottom -= (info.posTop + info.height)
            if(bottom <= 0)
                return it
        }
        return listInfo.lastIndex
    }

    override fun onVerticalScrollChanged(t: Int, oldt: Int) {
        Log.d(TAG, "onVerticalScrollChanged TOP:[$oldt]>>[$t]")
        positionY = t
    }

    override fun onHorizontalScrollChanged(l: Int, oldl: Int) {
        Log.d(TAG, "onHorizontalScrollChanged LEFT:[$oldl]>>[$l]")
        positionX = l
    }

    companion object {
        const val TAG = "[DE][VI] Canvas"
        const val DELAY_TIME = 200L
    }
}