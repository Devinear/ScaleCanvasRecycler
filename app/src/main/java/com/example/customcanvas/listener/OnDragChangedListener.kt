package com.example.customcanvas.listener

interface OnDragChangedListener {
    fun onDrag(dx: Float, dy: Float, focusX: Float, focusY: Float)
    fun onDragStart(focusX: Float, focusY: Float)
    fun onDragEnd()
}