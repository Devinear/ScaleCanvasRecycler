package com.example.customcanvas

interface OnScaleChangedListener {
    fun onScaleChange(scaleFactor: Float, focusX: Float, focusY: Float) : Boolean
    fun onScaleStart() : Boolean
    fun onScaleEnd()
}