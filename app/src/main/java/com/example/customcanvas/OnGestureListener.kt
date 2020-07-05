package com.example.customcanvas

import android.view.GestureDetector
import android.view.MotionEvent

class OnGestureListener() : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false

    override fun onShowPress(e: MotionEvent?)  = Unit

    override fun onDoubleTap(e: MotionEvent?): Boolean = false

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean = false

    override fun onSingleTapUp(e: MotionEvent?): Boolean = false

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean = false

    override fun onDown(e: MotionEvent?): Boolean = false

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false

    override fun onLongPress(e: MotionEvent) = Unit

    companion object {
        const val TAG = "[DE][GE] Gesture"
    }
}