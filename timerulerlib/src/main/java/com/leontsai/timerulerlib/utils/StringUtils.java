package com.leontsai.timerulerlib.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtils {

    public static String getFromRaw(Context context,int id){
        try {
            InputStreamReader inputReader = new InputStreamReader(context. getResources().openRawResource(id));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getFromAssets(Context context,String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader(context. getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String countingString(String s){
      String plain= new BigDecimal(s).toPlainString() ;
        DecimalFormat df = new DecimalFormat("#####0");
       return df.format(Double.parseDouble(plain));
    }


    public static String ymdhms="yyyyMMddHHmmss";

    public static String calendarString(Calendar calendar){
//        Calendar calendat = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat(ymdhms);

        return  sdf.format(calendar.getTime());
    }
    public static Calendar stringCalendar(String str){
        SimpleDateFormat sdf= new SimpleDateFormat(ymdhms);

        Date date = null;
        try {
            date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
