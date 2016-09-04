package net.almaak.tweets.auth;

import net.almaak.tweets.utils.TimeUtils;
import net.almaak.tweets.utils.auth.AuthorizationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leiferksn on 9/1/16.
 */

public class AuthorizationUtilsTest {

    private static final String CONSUMER_KEY = "r***********************B";
    private static final String CONSUMER_SECRET = "H************************************************t";
    private static final String ACCESS_TOKEN = "2************************************************n";
    private static final String ACCESS_TOKEN_SECRET = "S*******************************************y";
    private static final String HTTP_METHOD_GET = "GET";

    private Map<String, String> requestParameters = new HashMap<String, String>();
    private String httpRequestBaseURL = "https://api.twitter.com/1.1/statuses/user_timeline.json";


    /*
        public static String generateSingleUserAuthorizationHeader (
            final String consumerKey,
            final String consumerSecret,
            final String accessToken,
            final String accessTokenSecret,
            final Map<String, String> requestParameters,
            final String httpMethod,
            final String httpRequestBaseURL)
     */

    @Before
    public void setUp(){
        requestParameters.put("user_id", "almaak");
    }

    @Test
    public void shouldCreateValidAuthorizationHeader() throws Exception {
    String authorizationHeader = AuthorizationUtils.generateSingleUserAuthorizationHeader(CONSUMER_KEY,
            CONSUMER_SECRET,
            ACCESS_TOKEN,
            ACCESS_TOKEN_SECRET,
            requestParameters,
            HTTP_METHOD_GET,
            httpRequestBaseURL, TimeUtils.retrieveTimeOffsetToServer());

        Assert.assertNotNull(authorizationHeader);
    }

    @Test
    public void shouldReturnResponseFromTwitter() throws Exception {
        String authorizationHeader = AuthorizationUtils.generateSingleUserAuthorizationHeader(CONSUMER_KEY,
                CONSUMER_SECRET,
                ACCESS_TOKEN,
                ACCESS_TOKEN_SECRET,
                requestParameters,
                HTTP_METHOD_GET,
                httpRequestBaseURL, TimeUtils.retrieveTimeOffsetToServer());

        URL url = new URL(httpRequestBaseURL);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod(HTTP_METHOD_GET);
        con.setRequestProperty("Host", "api.twitter.com");
        con.setRequestProperty("User-Agent", "testing my application");
        con.setRequestProperty("Authorization", authorizationHeader);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        Assert.assertNotNull(con.getResponseMessage());
        Assert.assertTrue(con.getResponseCode() != 401);

        StringBuffer buf = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line = "";
        while((line = reader.readLine()) != null) {
            buf.append(line + "\r\n");
        }
        String response = buf.toString();
        Assert.assertTrue(!response.contains("errors"));
    }
}
