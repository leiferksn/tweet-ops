package net.almaak.tweets.auth;

import net.almaak.tweets.utils.auth.AuthorizationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        requestParameters.put("user_id", "drwhom");
    }

    @Test
    public void shouldCreateValidAuthorizationHeader() throws Exception {
    String authorizationHeader = AuthorizationUtils.generateSingleUserAuthorizationHeader(CONSUMER_KEY,
            CONSUMER_SECRET,
            ACCESS_TOKEN,
            ACCESS_TOKEN_SECRET,
            requestParameters,
            HTTP_METHOD_GET,
            httpRequestBaseURL);

        Assert.assertNotNull(authorizationHeader);
    }

}
