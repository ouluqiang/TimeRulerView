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
                    var times=extinfs[i].split(extinf)
                    var list= mutableListOf<String>()
                    for (j in times.indices) {
//                        Log.d("ss", "$i    ${times[j]}")
                        if (!times[j].isNullOrEmpty()){
                            list.add(times[j])
                        }
                    }
                    map.put(i,list)
                }
            }else{
                var times=s.split(extinf)
                for (j in times.indices) {
//                    Log.d("ss", "${times[j]}")
                    var list= arrayListOf<String>()
                    if (!times[j].isNullOrEmpty()){
                        list.add(times[j])
                    }
                    map.put(0,list)
                }
            }
            var mapTime= mutableMapOf<Int,MutableList<String>>()
            for (i in 0 until map.size) {
//                Log.d("ss", "${map[i]}")
                var list= arrayListOf<String>()
                var times=map[i]
                for (j in 0 until times!!.size) {
//                        var other=".ts"
                    if (times!!.size>1){
                        var dates=times[j].split(",")
                        var date=dates[1].substring(dates[1].lastIndexOf("/")+1,dates[1].lastIndexOf(".ts"))
                        list.add(date)
                    }else{
                        var dates=times[j].split(",")
                        var date=dates[1].substring(dates[1].lastIndexOf("/")+1,dates[1].lastIndexOf(".ts"))
                        var endDate=StringUtils.countingString((date.toLong()+dates[0].toDouble()).toString())
                        list.add(date)
                        list.add(endDate)
                    }
                }
                mapTime.put(i,list)
            }
            return mapTime
        }
        return null
    }



}

