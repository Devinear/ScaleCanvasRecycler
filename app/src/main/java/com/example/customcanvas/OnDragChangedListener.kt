package com.example.customcanvas

interface OnDragChangedListener {
    fun onDrag(dx: Float, dy: Float, focusX: Float, focusY: Float)
    fun onDragStart()
    fun onDragEnd()
}