package com.leontsai.timerulerlib.callback

import android.view.MotionEvent


internal interface OnActionListener {
    /**
     * 手指移动
     */
    fun onMove(distanceX: Float)
    fun onSingleTapUp(e: MotionEvent?)
}