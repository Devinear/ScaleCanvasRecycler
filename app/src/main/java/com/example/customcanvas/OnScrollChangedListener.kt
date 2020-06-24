package com.example.customcanvas

interface OnScrollChangedListener {
    fun onVerticalScrollChanged(t: Int, oldt: Int)
    fun onHorizontalScrollChanged(l: Int, oldl: Int)
}