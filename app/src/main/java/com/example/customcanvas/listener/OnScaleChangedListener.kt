package com.example.customcanvas.listener

interface OnScaleChangedListener {
    fun onScaleChange(scaleFactor: Float, focusX: Float, focusY: Float) : Boolean
    fun onScaleStart() : Boolean
    fun onScaleEnd()
}