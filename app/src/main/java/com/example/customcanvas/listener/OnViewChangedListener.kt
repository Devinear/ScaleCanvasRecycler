package com.example.customcanvas.listener

interface OnViewChangedListener {
    fun onViewSize(width: Int, height: Int, scale: Float)
    fun onFindItem(first: Int, last: Int)
    fun onPivotPoint(pivotX: Float, pivotY: Float)
}