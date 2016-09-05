package net.almaak.tweets.utils.auth;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

/**
 * Created by leiferksn on 9/1/16.
 *
 * Create authorization headers as defined in https://dev.twitter.com/oauth/overview/authorizing-requests
 */

public class AuthorizationUtils {

    private static final String OAUTH_VERSION = "1.0";
    private static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String URL_ENCODE_CHARSET = "UTF-8";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    /*
    For single user auth this should come into every request

    OAuth oauth_consumer_key="xvz1evFS4wEEPTGEFPHBog",
              oauth_nonce="kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg",
              oauth_signature="tnnArxj06cWHq44gCs1OSKk%2FjLY%3D",
              oauth_signature_method="HMAC-SHA1",
              oauth_timestamp="1318622958",
              oauth_token="370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb",
              oauth_version="1.0"
     */

    public static String generateSingleUserAuthorizationHeader(
            final String consumerKey,
            final String consumerSecret,
            final String accessToken,
            final String accessTokenSecret,
            final Map<String, String> requestParameters,
            final String httpMethod,
            final String httpRequestBaseURL,
            final Long serverTimeOffset) throws NoSuchAlgorithmException, UnsupportedEncodingException, SignatureException, InvalidKeyException {

        Map<String, String> authParameters = createAuthParamaters(consumerKey,
                consumerSecret,
                accessToken,
                accessTokenSecret,
                requestParameters,
                httpMethod,
                httpRequestBaseURL, serverTimeOffset);

        StringBuffer headerBuf = new StringBuffer();
        headerBuf.append("OAuth ");

        for(Map.Entry<String, String> entry: authParameters.entrySet()) {
            headerBuf.append(URLEncoder.encode(entry.getKey(), URL_ENCODE_CHARSET));
            headerBuf.append("=");
            headerBuf.append("\"");
            headerBuf.append(URLEncoder.encode(entry.getValue(), URL_ENCODE_CHARSET));
            headerBuf.append("\", ");
        }

        String headerValue = headerBuf.toString();
        int i = headerValue.lastIndexOf("\", ");
        headerValue = headerValue.substring(0, i + 1);
        return headerValue;
    }

    private static Map<String, String> createAuthParamaters(
            final String consumerKey,
            final String consumerSecret,
            final String accessToken,
            final String accessTokenSecret,
            final Map<String, String> requestParameters,
            final String httpMethod,
            final String httpRequestBaseURL, Long serverTimeOffset) throws NoSuchAlgorithmException, UnsupportedEncodingException, SignatureException, InvalidKeyException {

        Map<String, String> authParams = new LinkedHashMap<String, String>();
        authParams.put("oauth_consumer_key", consumerKey);
        byte[] randomBytes = new byte[32];
        new Random().nextBytes(randomBytes);
        authParams.put("oauth_nonce", Base64.encodeBase64String(randomBytes));
        authParams.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.getTimeInMillis();
        authParams.put("oauth_timestamp", Long.toString(cal.getTimeInMillis() + serverTimeOffset));
        authParams.put("oauth_token", accessToken);
        authParams.put("oauth_version", OAUTH_VERSION);
        String oauthSignature = createRequestSignature(accessTokenSecret,
                consumerSecret,
                authParams,
                requestParameters, httpMethod, httpRequestBaseURL);

        authParams.put("oauth_signature", oauthSignature);
        Map<String, String> sortedAuthParams = sortResultMap(authParams);
        return sortedAuthParams;
    }

    private static String createRequestSignature (
            final String accessTokenSecret,
            final String consumerSecret,
            final Map<String, String> authParameters,
            final Map<String, String> requestParameters,
            final String httpMethod,
            final String httpRequestBaseURL) throws UnsupportedEncodingException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {

        String parameterString = createParameterString(authParameters, requestParameters);
        StringBuffer buf = new StringBuffer();
        buf.append(httpMethod.toUpperCase());
        buf.append("&");
        buf.append(URLEncoder.encode(httpRequestBaseURL, URL_ENCODE_CHARSET));
        buf.append("&");
        buf.append(URLEncoder.encode(parameterString, URL_ENCODE_CHARSET));

        String signatureBaseString = buf.toString();

        StringBuffer signingKeyBuf = new StringBuffer();
        signingKeyBuf.append(URLEncoder.encode(consumerSecret, URL_ENCODE_CHARSET));
        signingKeyBuf.append("&");
        signingKeyBuf.append(URLEncoder.encode(accessTokenSecret, URL_ENCODE_CHARSET));
        String signingKey = signingKeyBuf.toString();

        byte[] hmacSignature = calculateRFC2104HMAC(signatureBaseString, signingKey);
        return URLEncoder.encode(new String(Base64.encodeBase64(hmacSignature)), URL_ENCODE_CHARSET);
    }

    private static String convertByteArrayToHexString(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }

    private static Map<String, String> prepareParametersForAssembling(final Map<String, String> parameters) throws  UnsupportedEncodingException {
        Map<String, String> m = new HashMap<String, String>();

        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            m.put(URLEncoder.encode(entry.getKey(), URL_ENCODE_CHARSET),
                    URLEncoder.encode(entry.getValue(), URL_ENCODE_CHARSET));
        }
        return m;
    }

    private static String createParameterString(final Map<String, String> authParameters,
                                                final Map<String, String> requestParameters) throws UnsupportedEncodingException {
        /*
            Percent encode every key and value that will be signed.
            Sort the list of parameters alphabetically[1] by encoded key[2].
            For each key/value pair:
            Append the encoded key to the output string.
            Append the ‘=’ character to the output string.
            Append the encoded value to the output string.
            If there are more key/value pairs remaining, append a ‘&’ character to the output string.
         */

        Map<String, String> resultMap = new HashMap<String, String>();

        // TODO check for duplicates?
        resultMap.putAll(prepareParametersForAssembling(authParameters));
        resultMap.putAll(prepareParametersForAssembling(requestParameters));

        Map<String, String> sortedResultMap = sortResultMap(resultMap);

        StringBuffer buf = new StringBuffer();
        for(Map.Entry<String, String> entry : sortedResultMap.entrySet()) {
            buf.append(entry.getKey());
            buf.append("=");
            buf.append(entry.getValue());
            buf.append("&");
        }

        String parameterString = buf.toString();
        parameterString = parameterString.substring(0, parameterString.length() - "&".length());
        return parameterString;
    }

    private static byte[] calculateRFC2104HMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return mac.doFinal(data.getBytes());
    }

    private static Map<String,String> sortResultMap(final Map<String, String> resultMap) {
        Map<String, String> sortedResultMap = new LinkedHashMap<String, String>();
        List<String> keys = new ArrayList<String>();
        keys.addAll(resultMap.keySet());
        Collections.sort(keys);

        for(String key : keys){
            sortedResultMap.put(key, resultMap.get(key));
        }
        return sortedResultMap;
    }



}
