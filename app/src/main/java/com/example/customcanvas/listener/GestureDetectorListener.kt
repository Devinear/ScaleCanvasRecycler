package com.example.customcanvas.listener

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class GestureDetectorListener(val listener: OnGestureListener) : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if(e1 == null || e2 == null)
            return false

        val x = if(abs(e1.rawX-e2.rawX) > SWIPE_MIN_DISTANCE) (e1.rawX-e2.rawX).toInt() else 0
        val y = if(abs(e1.rawY-e2.rawY) > SWIPE_MIN_DISTANCE) (e1.rawY-e2.rawY).toInt() else 0
        if(x == 0 && y == 0) return false

        val isX = abs(x) > abs(y)
        val isNext = if(isX) (e1.rawX > e2.rawX) else (e1.rawY > e2.rawY)

        listener.onFling(isX = isX, isNext = isNext)
        return true
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        if(e == null) return false
        listener.onDoubleTap(e)
        return true
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        if(e == null) return false
        listener.onSingleTap(e)
        return true
    }

    override fun onLongPress(e: MotionEvent) = listener.onLongPress(e)

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean = false

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean = false

    override fun onDown(e: MotionEvent?): Boolean = false

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false

    override fun onShowPress(e: MotionEvent?)  = Unit

    companion object {
        const val SWIPE_MIN_DISTANCE = 120
        const val TAG = "[DE][GE] Gesture"
    }
}