package com.example.customcanvas

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView

class CanvasScrollView : ScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    var scrollListener : OnScrollChangedListener? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        Log.d(TAG, "onScrollChanged LEFT:[$oldl]>>[$l] TOP:[$oldt]>>[$t]")
        super.onScrollChanged(l, t, oldl, oldt)
        scrollListener?.onVerticalScrollChanged(t = t, oldt = oldt)
    }

    override fun scrollTo(x: Int, y: Int) {
        Log.d(TAG, "scrollTo X:[$x] Y:[$y]")
        super.scrollTo(x, y)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent ACTION:[${ev?.action}]")
        return super.onTouchEvent(ev)
    }

    companion object {
        const val TAG = "[DE][SC] V-SCROLL"
    }
}