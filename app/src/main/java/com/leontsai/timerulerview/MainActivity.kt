package com.leontsai.timerulerview

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.leo.JsonString
import com.leontsai.timerulerlib.TimeRulerView
import com.leontsai.timerulerlib.bean.TimeInfo
import com.leontsai.timerulerlib.utils.StringUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        rulerView.onSelectTimeListener = object : TimeRulerView.OnSelectTimeListener {
            override fun onSelectTime(time: Long) {
                rulerView_tv.text = sdf.format(time)
            }

            override fun onClickYesterDay() {
                Toast.makeText(this@MainActivity,"点击昨天",Toast.LENGTH_LONG).show()
            }

            override fun onClickToDay() {
                Toast.makeText(this@MainActivity,"点击明天",Toast.LENGTH_LONG).show()
            }
        }


//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)

//        calendar.set(year,month,day,12,0,0)


       var map= JsonString.getJson(this)
        val timeInfos = arrayListOf<TimeInfo>()
        for (i in 0 until map!!.size){
            var start= map[i]?.get(0)
            var end= map[i]?.get(map[i]!!.size-1)
            val timeInfo = TimeInfo(StringUtils.stringCalendar(start), StringUtils.stringCalendar(end))
            timeInfos.add(timeInfo)
        }

//        val start0 = Calendar.getInstance()
//        start0.set(year, month, day, 9, 30, 0)
//        val end0 = Calendar.getInstance()
//        end0.set(year, month, day, 10, 40, 0)
//
//        val start1 = Calendar.getInstance()
//        start1.set(year, month, day, 11, 40, 0)
//        val end1 = Calendar.getInstance()
//        end1.set(year, month, day, 12, 10, 0)
//
//        val start2 = Calendar.getInstance()
//        start2.set(year, month, day, 13, 50, 0)
//        val end2 = Calendar.getInstance()
//        end2.set(year, month, day, 14, 20, 0)
//
//        val start3 = Calendar.getInstance()
//        start3.set(year, month, day, 16, 30, 0)
//        val end3 = Calendar.getInstance()
//        end3.set(year, month, day, 19, 40, 0)
//
//
//        val timeInfo0 = TimeInfo(start0, end0)
//        val timeInfo1 = TimeInfo(start1, end1)
//        val timeInfo2 = TimeInfo(start2, end2)
//        val timeInfo3 = TimeInfo(start3, end3)
//
//        val timeInfos = arrayListOf<TimeInfo>()
//        timeInfos.add(timeInfo0)
//        timeInfos.add(timeInfo1)
//        timeInfos.add(timeInfo2)
//        timeInfos.add(timeInfo3)

        for (i in 0 until timeInfos!!.size){
            Log.d("times","${StringUtils.calendarString(timeInfos[i].startTime)}   ${StringUtils.calendarString(timeInfos[i].endTime)}")
        }

        rulerView.timeInMillis = timeInfos[0].startTime.timeInMillis
        rulerView_tv.text = sdf.format(rulerView.timeInMillis)
        rulerView.timeInfos = timeInfos

    }


//    override fun setRequestedOrientation(requestedOrientation: Int) {
//        super.setRequestedOrientation(requestedOrientation)
//    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation: Int = newConfig.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("onConfigurationChanged", "-------------横屏-------------")
        } else {
            Log.i("onConfigurationChanged", "-------------竖屏-------------")
        }
        Log.i(
            "onConfigurationChanged",
            "onConfigurationChanged: $orientation"
        )
    }
}
