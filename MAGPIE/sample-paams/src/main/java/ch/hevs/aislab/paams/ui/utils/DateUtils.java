package ch.hevs.aislab.paams.ui.utils;


import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ch.hevs.aislab.paamsdemo.R;

public class DateUtils {

    private static final String sameYearFormat = "dd.MM";
    private static final String differentYearFormat = "dd.MM.YYYY";

    public static String formatDate(Context context, long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar valueTs = Calendar.getInstance();
        valueTs.setTime(new Date(timestamp));
        Date date = new Date(timestamp);
        if (now.get(Calendar.YEAR) == valueTs.get(Calendar.YEAR)) {
            if (now.get(Calendar.DAY_OF_YEAR) == valueTs.get(Calendar.DAY_OF_YEAR)) {
                return context.getString(R.string.today);
            } else if (now.get(Calendar.DAY_OF_YEAR) - 1 == valueTs.get(Calendar.DAY_OF_YEAR)) {
                return context.getString(R.string.yesterday);
            } else {
                return new SimpleDateFormat(sameYearFormat, Locale.getDefault()).format(date);
            }
        } else {
            return new SimpleDateFormat(differentYearFormat, Locale.getDefault()).format(date);
        }
    }
}
