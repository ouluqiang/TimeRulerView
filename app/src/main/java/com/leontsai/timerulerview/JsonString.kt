package com.leo



import android.content.Context
import android.util.ArrayMap
import android.util.Log
import androidx.lifecycle.Transformations.map
import com.leontsai.timerulerlib.utils.StringUtils
import java.math.BigDecimal
import kotlin.math.log
import kotlin.text.Typography.times

object JsonString {

    fun getJson(context:Context):MutableMap<Int,MutableList<String>>?{
       var json= StringUtils.getFromAssets(context,"20230106.m3u8")
        if (!json.isNullOrEmpty()){
           var discontinuity= "#EXT-X-DISCONTINUITY"
           var extinf= "#EXTINF:"
            var start=json.indexOf(discontinuity)
            var end=json.indexOf("#EXT-X-ENDLIST")
            var s=json.subSequence(start+discontinuity.length,end)
            var map= hashMapOf<Int,MutableList<String>>()
            if (s.contains(discontinuity)){
                var extinfs= s.split(discontinuity)
                for (i in extinfs.indices){
                    var times=extinfs[i].split(extinf).filter { !it.isNullOrEmpty() }
                    var list = getTimeList(times)
                    map.put(i,list)
                }
            }else{
                var times=s.split(extinf).filter { !it.isNullOrEmpty() }
                var list = getTimeList(times)
                map.put(0,list)
            }
            return map
        }
        return null
    }

    private fun getTimeList(times: List<String>): MutableList<String> {
        var list = mutableListOf<String>()
        if (times.size > 1) {
            list.add(getTime(times[0]))
            list.add(getTime(times[times.size - 1], true))
        } else {
            list.add(getTime(times[0]))
            list.add(getTime(times[0], true))
        }
        return list
    }

    private fun getTime(time:String,isEnd:Boolean=false):String{
        var dates=time.split(",")
        var date=dates[1].substring(dates[1].lastIndexOf("/")+1,dates[1].lastIndexOf(".ts"))
        var endDate=StringUtils.countingString((date.toLong()+dates[0].toDouble()).toString())
        if (isEnd){
            return endDate
        }else{
            return date
        }
    }

}

