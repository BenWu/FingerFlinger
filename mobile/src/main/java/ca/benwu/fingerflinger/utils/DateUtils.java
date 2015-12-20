package ca.benwu.fingerflinger.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by BenWu on 2015-12-19.
 */
public class DateUtils {

    public static String getDateStringFromMilliseconds(long dateMilliseconds, String outputFormat) {
        try {
            Date date = new Date(dateMilliseconds);
            SimpleDateFormat out = new SimpleDateFormat(outputFormat, Locale.CANADA);
            return out.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return String.valueOf(dateMilliseconds);
        }
    }
}
