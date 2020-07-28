package com.example.customcanvas.listener

import android.view.MotionEvent

interface OnGestureListener {
    fun onFling(isX: Boolean, isNext: Boolean)
    fun onSingleTap(ev: MotionEvent)
    fun onLongPress(ev: MotionEvent)
    fun onDoubleTap(ev: MotionEvent)
}