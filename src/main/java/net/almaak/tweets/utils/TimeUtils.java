package net.almaak.tweets.utils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

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
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            Date d = sdf.parse(dateValue.get(0));
            timeOffset = System.currentTimeMillis() - d.getTime();
        } else {
            // TODO: make specific exception
            con = null;
            throw new Exception("Time offset can't be retrieved. Please check your connection");
        }
        con = null;
        return timeOffset;
    }


}
