package com.leo


import android.content.Context
import android.util.ArrayMap
import android.util.Log
import androidx.lifecycle.Transformations.map
import com.leontsai.timerulerlib.bean.TimeInfo
import com.leontsai.timerulerlib.utils.StringUtils
import java.math.BigDecimal
import kotlin.math.log
import kotlin.text.Typography.times

object JsonString {

    val DISCONTINUITY = "#EXT-X-DISCONTINUITY"
    val EXTINF = "#EXTINF:"
    val ENDLIST = "#EXT-X-ENDLIST"

    fun getJson(context: Context): MutableList<TimeInfo> {
        var json = StringUtils.getFromAssets(context, "20230106.m3u8")
        var list = mutableListOf<TimeInfo>()
        if (!json.isNullOrEmpty()) {
            var start = json.indexOf(DISCONTINUITY)
            var end = json.indexOf(ENDLIST)
            var dateTime = json.substring(start + DISCONTINUITY.length, end)
            if (dateTime.contains(DISCONTINUITY)) {
                var extinfs = dateTime.split(DISCONTINUITY)
                extinfs.forEachIndexed { index, s ->
                    list.add(getTimeInfo(s))
                }
            } else {
                list.add(getTimeInfo(dateTime))
            }
        }
        return list
    }

    private fun getTimeInfo(s: String): TimeInfo {
        var times = s.split(EXTINF).filter { !it.isNullOrEmpty() }
        if (times.size > 1) {
            return TimeInfo(
                StringUtils.stringCalendar(getTime(times[0])),
                StringUtils.stringCalendar(getTime(times[times.size - 1], true))
            )
        } else {
            return TimeInfo(
                StringUtils.stringCalendar(getTime(times[0])),
                StringUtils.stringCalendar(getTime(times[0], true))
            )
        }
    }

    private fun getTime(time: String, isEnd: Boolean = false): String {
        var dates = time.split(",")
        var date = dates[1].substring(dates[1].lastIndexOf("/") + 1, dates[1].lastIndexOf(".ts"))
        var endDate = StringUtils.countingString((date.toLong() + dates[0].toDouble()).toString())
        if (isEnd) {
            return endDate
        } else {
            return date
        }
    }

}

