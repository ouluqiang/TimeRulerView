package com.leontsai.timerulerlib

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.text.TextPaint
import android.util.Log
import androidx.annotation.NonNull
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.leontsai.timerulerlib.bean.ScaleInfo
import com.leontsai.timerulerlib.bean.TimeInfo
import com.leontsai.timerulerlib.callback.OnActionListener
import com.leontsai.timerulerlib.callback.OnTouchListener
import com.leontsai.timerulerlib.utils.StringUtils
import java.util.*


class TimeRulerView(private val mContext: Context, attrs: AttributeSet?) : View(mContext, attrs), OnActionListener {

    private var mGestureDetector = GestureDetector(context, OnTouchListener(this))

    /**
     * 24小时所分的总格数
     */
    private var mTotalCellNum = 144
    private var num = 144
    private var lattice = 6
    /**
     * 文字的字体大小
     */
    private var mTextFontSize = 0f
    /**
     * 文字的颜色
     */
    private var mTextColor = 0
    /**
     * 刻度的颜色
     */
    private var mScaleColor = 0
    /**
     * 顶部的线的颜色
     */
    private var mTopLineColor = 0
    /**
     * 底部的线的颜色
     */
    private var mBottomLineColor = 0
    /**
     * 选择时间段背景颜色
     */
    private var mSelectBackgroundColor = 0
    /**
     * 中间线的颜色
     */
    private var mMiddleLineColor = 0
    /**
     * 顶部线画笔
     */
    private val mTopLinePaint by lazy {
        Paint()
    }
    /**
     * 底部线画笔
     */
    private val mBottomLinePaint by lazy {
        Paint()
    }
    /**
     * 刻度线画笔
     */
    private val mScaleLinePaint by lazy {
        Paint()
    }
    /**
     * 中间线画笔
     */
    private val mMiddleLinePaint by lazy {
        Paint()
    }
    /**
     * 选择时间段的画笔
     */
    private val mSelectPaint by lazy {
        Paint()
    }
    /**
     * 文字画笔
     */
    private val mTextPaint by lazy {
        TextPaint()
    }
    /**
     * 顶部线的粗度
     */
    private var mTopLineStrokeWidth = 0f
    /**
     * 底部线的粗度
     */
    private var mBottomLineStrokeWidth = 0f
    /**
     * 刻度线的粗度
     */
    private var mScaleLineStrokeWidth = 0f
    /**
     * 中间线的粗度
     */
    private var mMiddleLineStrokeWidth = 0f
    /**
     * 每一格在屏幕上显示的长度
     */
    private var mWidthPerCell = 0f
    /**
     * 每一格代表的毫秒数
     */
    private var mMillisecondPerCell = 24 * 3600 * 1000 / mTotalCellNum

    /**
     * 手指移动的距离
     */
    private var mMoveDistance = 0f
    /**
     * 一天的时间所占的总的像素
     */
    private var totalPixelPerDay = mTotalCellNum * mWidthPerCell
    /**
     * 每一像素所占的毫秒数
     */
    private var mMillisecondPerPixel = 0f
    /**
     * 中间条绑定的日历对象
     */
    private var mCalendar = Calendar.getInstance()
    private var mInitCalendar = Calendar.getInstance()
    /**
     * 中间条绑定的日历对象对应的毫秒数
     */
    var timeInMillis = 0L
        get() {
            return mCalendar.timeInMillis
        }
        set(value) {
            field = value
//            mInitCalendar=mCalendar
            mCalendar.timeInMillis = field
            initMillisecond = mCalendar.timeInMillis
            initData()
            invalidate()
        }
    /**
     * 中间条绑定的日历对象初始毫秒值
     */
    private var initMillisecond = 0L
    /**
     * 中间条的X坐标
     */
    private var mMiddleLineX = 0F
    /**
     * 需要播放的时间段列表
     */
    var timeInfos = mutableListOf<TimeInfo>()
        set(value) {
            field = value
            invalidate()
        }

    private var scaleList = mutableListOf<ScaleInfo>()

    /**
     * 回放时间段最小时间点
     */
    private val mMinTime: Long = 0
    /**
     * 回放时间段最大时间点
     */
    private val mMaxTime: Long = 0
    private val shrink_or_magnify: Boolean = false
    private val scaleRatio = 1f

    constructor(mContext: Context) : this(mContext, null)

    init {
        Utils.init(mContext)
        isFocusable = true
        isClickable = true
        setOnTouchListener { _: View, ev: MotionEvent ->
            mGestureDetector.onTouchEvent(ev)
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeRulerView)
        initDefaultValue(typedArray)
        typedArray.recycle()
        initPaint()
        initData()
    }

    private fun initData() {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)
        mInitCalendar.set(year, month, day, 12, 0, 0)
        initMillisecond = mInitCalendar.timeInMillis
        mMoveDistance = (initMillisecond -mCalendar.timeInMillis) /mMillisecondPerPixel
        scaleList.clear()
        val setTime = Calendar.getInstance()
        for (i in 0..mTotalCellNum) {
            setTime.set(
                year, month, day, if (mTotalCellNum == num) {
                    i / lattice
                } else i,
                if (mTotalCellNum == num) {
                    if (i % lattice == 0) 0 else {
                        (i % lattice)*10
                    }
                } else 0, 0
            )
            val scaleInfo = ScaleInfo()
//            Log.d("a","${StringUtils.calendarString(setTime)}")
            scaleInfo.time = setTime.timeInMillis
            scaleInfo.text = i
            scaleList.add(scaleInfo)
        }
    }

    private fun initDefaultValue(@NonNull typedArray: TypedArray) {
        mTotalCellNum = typedArray.getInt(R.styleable.TimeRulerView_totalTimePerCell, num)
        mTextFontSize =
                typedArray.getDimension(R.styleable.TimeRulerView_textFontSize, ConvertUtils.sp2px(13f).toFloat())
        mTextColor = typedArray.getColor(R.styleable.TimeRulerView_textColor, Color.rgb(0, 0, 0))
        mScaleColor = typedArray.getColor(R.styleable.TimeRulerView_scaleColor, Color.rgb(0, 0, 0))
        mTopLineColor = typedArray.getColor(R.styleable.TimeRulerView_topLineColor, Color.rgb(0, 0, 0))
        mBottomLineColor = typedArray.getColor(R.styleable.TimeRulerView_bottomLineColor, Color.rgb(0, 0, 0))
        mMiddleLineColor = typedArray.getColor(R.styleable.TimeRulerView_middleLineColor, Color.rgb(0, 0, 0))
        mSelectBackgroundColor =
                typedArray.getColor(R.styleable.TimeRulerView_selectBackgroundColor, Color.rgb(255, 0, 0))
        mTopLineStrokeWidth =
                typedArray.getDimension(
                    R.styleable.TimeRulerView_topLineStrokeWidth
                    , ConvertUtils.dp2px(3f).toFloat()
                )
        mBottomLineStrokeWidth =
                typedArray.getDimension(
                    R.styleable.TimeRulerView_bottomLineStrokeWidth,
                    ConvertUtils.dp2px(3f).toFloat()
                )
        mScaleLineStrokeWidth = typedArray.getDimension(
            R.styleable.TimeRulerView_scaleLineStrokeWidth,
            ConvertUtils.dp2px(1f).toFloat()
        )
        mMiddleLineStrokeWidth = typedArray.getDimension(
            R.styleable.TimeRulerView_middleLineStrokeWidth,
            ConvertUtils.dp2px(2f).toFloat()
        )
        mWidthPerCell =
                typedArray.getDimension(R.styleable.TimeRulerView_widthPerCell, ConvertUtils.dp2px(20f).toFloat())

        mMillisecondPerPixel = mMillisecondPerCell / mWidthPerCell
        totalPixelPerDay = mTotalCellNum * mWidthPerCell
    }

    private fun initPaint() {
        mTopLinePaint.isAntiAlias = true
        mTopLinePaint.color = mTopLineColor
        mTopLinePaint.style = Paint.Style.STROKE
        mTopLinePaint.strokeWidth = mTopLineStrokeWidth

        mBottomLinePaint.isAntiAlias = true
        mBottomLinePaint.color = mBottomLineColor
        mBottomLinePaint.style = Paint.Style.STROKE
        mBottomLinePaint.strokeWidth = mBottomLineStrokeWidth

        mScaleLinePaint.isAntiAlias = true
        mScaleLinePaint.color = mScaleColor
        mScaleLinePaint.style = Paint.Style.STROKE
        mScaleLinePaint.strokeWidth = mScaleLineStrokeWidth

        mMiddleLinePaint.isAntiAlias = true
        mMiddleLinePaint.color = mMiddleLineColor
        mMiddleLinePaint.style = Paint.Style.STROKE
        mMiddleLinePaint.strokeWidth = mMiddleLineStrokeWidth

        mSelectPaint.isAntiAlias = true
        mSelectPaint.color = mSelectBackgroundColor
        mSelectPaint.style = Paint.Style.FILL

        mTextPaint.isAntiAlias = true
        mTextPaint.color = mTextColor
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = mTextFontSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mMiddleLineX = measuredWidth / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawTimeInfos(canvas!!)
        drawTopLine(canvas)
        drawBottomLine(canvas)
        drawScaleLine(canvas)
        drawMiddleLine(canvas)
    }

    /**
     * 顶线
     */
    private fun drawTopLine(canvas: Canvas) {
        canvas.drawLine(
            0f, 0f, measuredWidth.toFloat(), 0f, mTopLinePaint
        )
    }

    /**
     * 底线
     */
    private fun drawBottomLine(canvas: Canvas) {
        canvas.drawLine(
            0f,
            measuredHeight.toFloat(),
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            mBottomLinePaint
        )
    }

    /**
     * 中间线
     */
    private fun drawMiddleLine(canvas: Canvas) {
        canvas.drawLine(
            measuredWidth / 2f,
            mTextFontSize+ ConvertUtils.dp2px(4f),
            measuredWidth / 2f,
            measuredHeight.toFloat(),
            mMiddleLinePaint
        )
    }

    /**
     * 刻度
     */
    private fun drawScaleLine(canvas: Canvas) {
        for (i in 0..mTotalCellNum) {
            val time = scaleList[i].time
            val distance = Math.abs((time - mCalendar.timeInMillis) / mMillisecondPerPixel)
            if (distance < measuredWidth / 2) {
                val XFromMiddlePoint =
                    if (time < mCalendar.timeInMillis) measuredWidth / 2 - distance else measuredWidth / 2 + distance
                if (i % lattice == 0) {
                    canvas.drawLine(
                        XFromMiddlePoint.toFloat()
//                    , (measuredHeight - ConvertUtils.dp2px(5f)).toFloat()
                        ,
                        mTextFontSize + ConvertUtils.dp2px(8f),
                        XFromMiddlePoint.toFloat(),
                        (measuredHeight.toFloat() - ConvertUtils.dp2px(8f)).toFloat(),
                        mScaleLinePaint
                    )
                }else {
                    canvas.drawLine(
                        XFromMiddlePoint.toFloat()
//                    , (measuredHeight - ConvertUtils.dp2px(5f)).toFloat()
                        ,
                        mTextFontSize + ConvertUtils.dp2px(15f),
                        XFromMiddlePoint.toFloat(),
                        (measuredHeight.toFloat() - ConvertUtils.dp2px(15f)).toFloat(),
                        mScaleLinePaint
                    )
                }
                drawText(i, XFromMiddlePoint.toFloat(), canvas)
            }
        }
    }


    /**
     * 渲染可回放时间段
     */
    fun drawTimeInfos(canvas: Canvas) {
        val currentMillis = mCalendar.timeInMillis
        //半个View长度所占的毫秒数
        val halfViewWidthMillis = (measuredWidth / 2) * mMillisecondPerPixel
        //最左边时刻条绑定的毫秒数
        val leftMillis = currentMillis - halfViewWidthMillis
        //最右边时刻条绑定的毫秒数
        val rightMillis = currentMillis + halfViewWidthMillis

        timeInfos.forEach {
            val startOffsetPixel = (it.startTime.timeInMillis - currentMillis) / mMillisecondPerPixel
            val endOffsetPixel = (it.endTime.timeInMillis - currentMillis) / mMillisecondPerPixel
            if (rightMillis > it.endTime.timeInMillis && leftMillis < it.startTime.timeInMillis) {
                //时间段在屏幕所容纳的刻度尺中间
                canvas.drawRect(
                    mMiddleLineX + startOffsetPixel,
                    0f,
                    mMiddleLineX + endOffsetPixel,
                    measuredHeight.toFloat(),
                    mSelectPaint
                )
            } else if (rightMillis <= it.endTime.timeInMillis && rightMillis >= it.startTime.timeInMillis) {
                //时间段在屏幕所容纳的刻度尺右边
                canvas.drawRect(
                    mMiddleLineX + startOffsetPixel,
                    0f,
                    measuredWidth.toFloat(),
                    measuredHeight.toFloat(),
                    mSelectPaint
                )
            } else if (leftMillis <= it.endTime.timeInMillis && leftMillis >= it.startTime.timeInMillis) {
                //时间段在屏幕所容纳的刻度尺左边
                canvas.drawRect(
                    0f,
                    0f,
                    mMiddleLineX + endOffsetPixel,
                    measuredHeight.toFloat(),
                    mSelectPaint
                )
            }
        }

    }

    private fun drawText(i: Int, moveX: Float, canvas: Canvas) {
        val fontMetrics = mTextPaint.fontMetrics
        val top = fontMetrics.top//为基线到字体上边框的距离,即上图中的top
        val bottom = fontMetrics.bottom//为基线到字体下边框的距离,即上图中的bottom
        val baseLineY = measuredHeight / 2 - top / 2 - bottom / 2//基线中间点的y轴计算公式

        val timeLineY =mTextFontSize+ConvertUtils.dp2px(2f)
            if (mTotalCellNum == num) {
            if (i % lattice == 0) {
                if (i < lattice*10) {
                    canvas.drawText("0" + (i / lattice).toString() + ":00", moveX, timeLineY, mTextPaint)
                } else {
                    canvas.drawText((i / lattice).toString() + ":00", moveX, timeLineY, mTextPaint)
                }
            }
        } else if (mTotalCellNum == 24) {
            if (i < 10) {
                canvas.drawText("0$i:00", moveX, timeLineY, mTextPaint)
            } else {
                canvas.drawText("$i:00", moveX, timeLineY, mTextPaint)
            }
        }


        if (i==0){
            val time = scaleList[0].time
            var left=StringUtils.countingString((time-(measuredWidth / 2/2* mMillisecondPerPixel)).toString())
            val distance = Math.abs((left.toLong() - mCalendar.timeInMillis) / mMillisecondPerPixel)
            val leftX =
                if (left.toLong() < mCalendar.timeInMillis) measuredWidth / 2 - distance else measuredWidth / 2 + distance
            canvas.drawText("昨天", leftX, baseLineY, mTextPaint)
        }else if (i==scaleList.size-1){
            val time = scaleList[scaleList.size-1].time
            var right=StringUtils.countingString((time+(measuredWidth / 2+measuredWidth /2/2* mMillisecondPerPixel)).toString())
            val distance = Math.abs((right.toLong() - mCalendar.timeInMillis) / mMillisecondPerPixel)
            val rightX =
                if (right.toLong() < mCalendar.timeInMillis) measuredWidth / 2 - distance else measuredWidth / 2 + distance
            canvas.drawText("明天", rightX, baseLineY, mTextPaint)
        }

    }
    var isScroll=false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
//            MotionEvent.ACTION_DOWN->{
//                Log.i("down","down")
//            }
//            MotionEvent.ACTION_MOVE->{
//                Log.i("MOVE","MOVE")
//            }
//            MotionEvent.ACTION_CANCEL->{
//            }
            MotionEvent.ACTION_UP->{
                Log.i("UP","UP")
                if (isScroll){
                    onSelectTimeListener!!.onUpTime(mCalendar.timeInMillis)
                    isScroll=false
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onMove(distanceX: Float) {
        mMoveDistance += distanceX
        mCalendar.timeInMillis = initMillisecond - (mMoveDistance * mMillisecondPerPixel).toLong()
//        Log.i("Move", "onScroll-:$mMoveDistance     ${totalPixelPerDay/2}")
        var start=scaleList.get(0).time
        var end=scaleList.get(scaleList.size-1).time
        if (mCalendar.timeInMillis<start){
            mMoveDistance= totalPixelPerDay/2
            if (onSelectTimeListener != null) {
                mCalendar.timeInMillis=start
                val time = mCalendar.timeInMillis
                onSelectTimeListener!!.onSelectTime(time)
            }
        }else if (mCalendar.timeInMillis>end){
            mMoveDistance= -(totalPixelPerDay/2)
            if (onSelectTimeListener != null) {
                mCalendar.timeInMillis=end
                val time = mCalendar.timeInMillis
                onSelectTimeListener!!.onSelectTime(time)
            }
        }else{
            if (onSelectTimeListener != null) {
                val time = mCalendar.timeInMillis
                onSelectTimeListener!!.onSelectTime(time)
            }
        }
        isScroll=true

        invalidate()
    }


    override fun onSingleTapUp(e: MotionEvent?) {

       var halfW= measuredWidth / 2
        val yesterTime = scaleList[0].time
        val yesterDistance = Math.abs((yesterTime - mCalendar.timeInMillis) / mMillisecondPerPixel)
        var yesterW=halfW-yesterDistance
        val toTime = scaleList[scaleList.size-1].time
        val toDistance = Math.abs((toTime - mCalendar.timeInMillis) / mMillisecondPerPixel)
        var toW=halfW+toDistance

       var yH= bottom-top
        when(e?.action){
            MotionEvent.ACTION_UP ->{
                Log.i("cyl", "onDown X:${e?.x}  yesterW:${yesterW} toW:${toW} Y:${e?.y}  yH:${yH}  ")
                if (e?.x>0&&e?.x<yesterW&&e?.y>0&&e?.y<yH ){
                    Log.i("cyl", "昨天 ${e?.x} ${e?.y} ${yesterW} ${yH}")
                    onSelectTimeListener?.onClickYesterDay()
                }else if (e?.x>toW&&e?.x<measuredWidth&&e?.y>0&&e?.y<yH ){
                    Log.i("cyl", "明天 ${e?.x} ${e?.y} ${toW} ${yH} ")
                    onSelectTimeListener?.onClickToDay()
                }
            }
        }

    }


    var onSelectTimeListener: OnSelectTimeListener? = null

    interface OnSelectTimeListener {
        fun onSelectTime(time: Long)
        fun onUpTime(time: Long)
        fun onClickYesterDay()
        fun onClickToDay()
    }
}