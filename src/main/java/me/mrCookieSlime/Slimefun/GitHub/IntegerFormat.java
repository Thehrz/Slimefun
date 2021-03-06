package me.mrCookieSlime.Slimefun.GitHub;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IntegerFormat {
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatBigNumber(int i) {
        return NumberFormat.getNumberInstance(Locale.US).format(i);
    }

    public static Date parseGitHubDate(String str) {
        try {
            return date_format.parse(str.replace("T", " ").replace("Z", ""));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String timeDelta(Date date) {
        long timestamp = date.getTime();
        int hours = (int) ((System.currentTimeMillis() - timestamp) / 3600000L);

        if (hours == 0) {
            return "> 1h";
        }
        if (hours / 24 == 0) {
            return (hours % 24) + "h";
        }
        if (hours % 24 == 0) {
            return (hours / 24) + "d";
        }

        return (hours / 24) + "d " + (hours % 24) + "h";
    }
}


