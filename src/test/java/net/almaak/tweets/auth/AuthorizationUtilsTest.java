package net.almaak.tweets.auth;

import net.almaak.tweets.utils.TimeUtils;
import net.almaak.tweets.utils.auth.AuthorizationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leiferksn on 9/1/16.
 *
 * The test checks if the oauth signature is valid based on the user's authorization data.
 * Using the service provided by http://term.ie/.
 *
 */

public class AuthorizationUtilsTest {

    private static final String CONSUMER_KEY = "key";
    private static final String CONSUMER_SECRET = "secret";
    private static final String ACCESS_TOKEN = "accesskey";
    private static final String ACCESS_TOKEN_SECRET = "accesssecret";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String CUSTOM_REQUEST_PARAM = "user_id";
    private static final String CUSTOM_REQUEST_PARAM_VALUE = "test";

    private Map<String, String> requestParameters = new HashMap<String, String>();
    private String httpRequestBaseURL = "http://term.ie/oauth/example/echo_api.php";

    @Before
    public void setUp(){
        requestParameters.put(CUSTOM_REQUEST_PARAM, CUSTOM_REQUEST_PARAM_VALUE);
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

    private String createRequestURL(final String requestBaseURL, final String authorizationHeader) {
        StringBuffer urlBuf = new StringBuffer(requestBaseURL);
        String authorizationHeaderToSplit = authorizationHeader.replace("OAuth ", "");
        String[] authorizationParametersPairs = authorizationHeaderToSplit.split(",");
        urlBuf.append("?");
        urlBuf.append(CUSTOM_REQUEST_PARAM);
        urlBuf.append("=");
        urlBuf.append(requestParameters.get(CUSTOM_REQUEST_PARAM));

        for(String authParamPair : authorizationParametersPairs) {
            urlBuf.append("&");
            urlBuf.append(authParamPair.replaceAll("\"", "").trim());
        }
        return urlBuf.toString();
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

        String requestUrl = createRequestURL(httpRequestBaseURL, authorizationHeader);
        URL url = new URL(requestUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        Assert.assertNotNull(con.getResponseMessage());
        Assert.assertTrue(con.getResponseCode() != 401);

        StringBuffer buf = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line = "";
        while((line = reader.readLine()) != null) {
            buf.append(line + "\r\n");
        }
        String response = buf.toString().replaceAll("\\r\\n", "");
        Assert.assertTrue(response.equalsIgnoreCase(CUSTOM_REQUEST_PARAM + "=" + requestParameters.get(CUSTOM_REQUEST_PARAM)));
    }
}
