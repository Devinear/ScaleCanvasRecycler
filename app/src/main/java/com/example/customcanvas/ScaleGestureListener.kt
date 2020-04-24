package com.example.customcanvas

import android.util.Log
import android.view.ScaleGestureDetector

class ScaleGestureListener(val listener: OnScaleChangedListener) : ScaleGestureDetector.OnScaleGestureListener {

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        Log.d(TAG, "onScaleBegin")
        return listener.onScaleStart()
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        Log.d(TAG, "onScaleEnd")
        listener.onScaleEnd()
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        val scaleFactor = detector?.scaleFactor?:0f
        if(scaleFactor.isNaN() || scaleFactor.isInfinite() || detector == null || scaleFactor < 0) {
            return false
        }
        Log.d(TAG, "onScale factor:$scaleFactor focusX:${detector.focusX} focusY:${detector.focusY}")
        return listener.onScaleChange(scaleFactor, detector.focusX, detector.focusY)
    }

    companion object {
        const val TAG = "[DE][GE] Scale"
    }
}