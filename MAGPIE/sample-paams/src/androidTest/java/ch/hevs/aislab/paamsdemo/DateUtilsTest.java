package ch.hevs.aislab.paamsdemo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//import ch.hevs.aislab.paams.ui.utils.DateUtils;

import ch.hevs.aislab.paams.ui.utils.DateUtils;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DateUtilsTest {

    Context context;
    Date now;

    @Before
    public void getContext() {
        context = InstrumentationRegistry.getTargetContext();
        now = new Date();
    }

    @Test
    public void todayTest() {
        String todayResource = context.getString(R.string.today);
        String todayUtils = DateUtils.formatDate(context, now.getTime());
        assertEquals(todayResource, todayUtils);
    }

    @Test
    public void yesterdayTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long yesterdayTs = calendar.getTimeInMillis();
        String yesterdayResource = context.getString(R.string.yesterday);
        String yesterdayUtils = DateUtils.formatDate(context, yesterdayTs);
        assertEquals(yesterdayResource, yesterdayUtils);
    }

    @Test
    public void sameYearTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        long beforeYdayTs = calendar.getTimeInMillis();
        String beforeYday = new SimpleDateFormat("dd.MM", Locale.getDefault()).format(beforeYdayTs);
        String beforeYdayUtils = DateUtils.formatDate(context, beforeYdayTs);
        assertEquals(beforeYday, beforeYdayUtils);
    }

    @Test
    public void differentYearTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        long lastYearTs = calendar.getTimeInMillis();
        String lastYear = new SimpleDateFormat("dd.MM.YYY", Locale.getDefault()).format(lastYearTs);
        String lastYearUtils = DateUtils.formatDate(context, lastYearTs);
        assertEquals(lastYear, lastYearUtils);
    }
}
