package ch.hevs.aislab.paams.ui.utils;


import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ch.hevs.aislab.paamsdemo.R;

public class DateUtils {

    public static String formatDate(Context context, String format, long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar valueTs = Calendar.getInstance();
        valueTs.setTime(new Date(timestamp));
        if ( (now.get(Calendar.YEAR) == valueTs.get(Calendar.YEAR)) &&
                (now.get(Calendar.DAY_OF_YEAR) == valueTs.get(Calendar.DAY_OF_YEAR)) ) {
            return context.getString(R.string.today);
        } else {
            Date date = new Date(timestamp);
            return new SimpleDateFormat(format, Locale.getDefault()).format(date);
        }
    }
}
