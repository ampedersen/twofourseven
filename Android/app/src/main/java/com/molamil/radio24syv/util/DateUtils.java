package com.molamil.radio24syv.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by patriksvensson on 25/01/16.
 */
public class DateUtils
{
    //HELPER
    public static Date timeStringToDate(String timeStr)
    {
        if(timeStr == null)
        {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {

            date = formatter.parse(timeStr);
            //System.out.println(date);
            //System.out.println(formatter.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
