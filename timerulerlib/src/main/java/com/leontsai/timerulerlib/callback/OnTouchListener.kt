package com.leontsai.timerulerlib.callback

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent


internal class OnTouchListener(val listener: OnActionListener) : GestureDetector.SimpleOnGestureListener() {

    @Volatile
    private var filterFirstMove = true

    override fun onDown(e: MotionEvent?): Boolean {
        Log.i("cyl", "onDown")
        filterFirstMove = true
//        listener.onDown(e)
        return super.onDown(e)
    }


    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        listener.onSingleTapUp(e)
        return super.onSingleTapUp(e)
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        Log.i("cyl", "onScroll---->distanceX:$distanceX   distanceY: $distanceY   e1:${e1?.action} ${MotionEvent.ACTION_UP}")
        if (Math.abs(distanceX) <= 0.2f) return false

        //第一个distanceX有bug,不能用，过滤掉
        if (filterFirstMove) {
            filterFirstMove = false
            return false
        }
        listener.onMove(-distanceX)
        return super.onScroll(e1, e2, distanceX, distanceY)
    }
}