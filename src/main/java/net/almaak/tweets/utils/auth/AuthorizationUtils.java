package net.almaak.tweets.utils.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by leiferksn on 9/1/16.
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

    public static String generateSingleUserAuthorizationHeader (
            final String consumerKey,
            final String consumerSecret,
            final String accessToken,
            final String accessTokenSecret,
            final Map<String, String> requestParameters,
            final String httpMethod,
            final String httpRequestBaseURL) throws NoSuchAlgorithmException, UnsupportedEncodingException, SignatureException, InvalidKeyException {

        Map<String, String> authParameters = createAuthParamaters(consumerKey,
                consumerSecret,
                accessToken,
                accessTokenSecret,
                requestParameters,
                httpMethod,
                httpRequestBaseURL);

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
        headerValue = headerValue.substring(0, i);
        return headerValue;
    }

    private static Map<String, String> createAuthParamaters (
            final String consumerKey,
            final String consumerSecret,
            final String accessToken,
            final String accessTokenSecret,
            final Map<String, String> requestParameters,
            final String httpMethod,
            final String httpRequestBaseURL) throws NoSuchAlgorithmException, UnsupportedEncodingException, SignatureException, InvalidKeyException {

        Map<String, String> authParams = new HashMap<String, String>();
        authParams.put("oauth_consumer_key", consumerKey);
        authParams.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
        authParams.put("oauth_timestamp", Long.toString(System.currentTimeMillis()));
        authParams.put("oauth_token", accessToken);
        authParams.put("oauth_version", OAUTH_VERSION);

        byte[] randomBytes = new byte[32];
        new Random().nextBytes(randomBytes);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(randomBytes);
        authParams.put("oauth_nonce", convertByteArrayToHexString(md5Bytes));
        String oauthSignature = createRequestSignature(accessTokenSecret,
                consumerSecret,
                authParams,
                requestParameters, httpMethod, httpRequestBaseURL);

        authParams.put("oauth_signature", oauthSignature);
        return authParams;
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
        buf.append(parameterString);

        String signatureBaseString = buf.toString();

        StringBuffer signingKeyBuf = new StringBuffer();
        signingKeyBuf.append(URLEncoder.encode(consumerSecret, URL_ENCODE_CHARSET));
        signingKeyBuf.append("&");
        signingKeyBuf.append(URLEncoder.encode(accessTokenSecret, URL_ENCODE_CHARSET));
        String signingKey = signingKeyBuf.toString();

        return calculateRFC2104HMAC(signatureBaseString, signingKey);
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

        // TODO check for duplicates
        resultMap.putAll(prepareParametersForAssembling(authParameters));
        resultMap.putAll(prepareParametersForAssembling(requestParameters));

        // TODO sort map

        StringBuffer buf = new StringBuffer();
        for(Map.Entry<String, String> entry : resultMap.entrySet()) {
            buf.append(entry.getKey());
            buf.append("=");
            buf.append(entry.getValue());
            buf.append("&");
        }

        String parameterString = buf.toString();
        parameterString = parameterString.substring(0, parameterString.length() - "&".length());
        return parameterString;
    }

    public static String calculateRFC2104HMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return convertByteArrayToHexString(mac.doFinal(data.getBytes()));
    }

}
