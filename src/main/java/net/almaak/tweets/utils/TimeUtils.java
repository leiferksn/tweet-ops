package net.almaak.tweets.utils;

import sun.net.www.protocol.http.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by leiferksn on 9/5/16.
 */
public class TimeUtils {

    public static Long retrieveTimeOffsetToServer() throws Exception {
        Long timeOffset = 0L;

        URL url = new URL("https://dev.twitter.com");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        Map<String, List<String>> headerFileds = con.getHeaderFields();

        List<String> dateValue = headerFileds.get("date");
        if(dateValue != null) {
            // Sun, 04 Sep 2016 21:47:14 GMT
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date d = sdf.parse(dateValue.get(0));
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("-02:00"));
            cal.setTime(d);
            timeOffset = System.currentTimeMillis() - cal.getTimeInMillis();
        } else {
            // TODO: make specific exception
            con = null;
            throw new Exception("Time offset can't be retrieved. Please check your connection");
        }
        con = null;
        return timeOffset;
    }


}
