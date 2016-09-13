package net.almaak.tweets.service;

import net.almaak.tweets.app.conf.AuthConfiguration;
import net.almaak.tweets.utils.TimeUtils;
import net.almaak.tweets.utils.auth.AuthorizationUtils;

import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Service implementations of twitter operations.
 *
 * Created by leiferksn on 9/4/16.
 *
 */
public class TwitterOperationsServiceImpl implements TwitterOperationsService {
    private static final String DELETE_RESOURCE_URL = "https://api.twitter.com/1.1/statuses/destroy/%s.json";

    @Override
    public boolean deleteTweets(List<String> tweetIds) {
        Long timeOffset = 0L;
        boolean success = true;

        try {
            timeOffset = TimeUtils.retrieveTimeOffsetToServer();
        } catch (Exception e) {
            // TODO: consider breaking the whole thing if no connection to twitter can't be obtained
            throw new RuntimeException(e);
        }

        for(String tweetId : tweetIds) {
            HttpsURLConnection urlConnection = null;
            try {
                String url = String.format(DELETE_RESOURCE_URL, tweetId);
                String httpMethod = "POST";

                String authorizationHeader = AuthorizationUtils.generateSingleUserAuthorizationHeader(httpMethod,
                        url,
                        timeOffset,
                        AuthConfiguration.getInstance(), null);

                URL deleteURL = new URL(url);

                urlConnection = (HttpsURLConnection) deleteURL.openConnection();
                urlConnection.setRequestMethod(httpMethod);
                urlConnection.setRequestProperty("Host", "api.twitter.com");
                urlConnection.setRequestProperty("User-Agent", "tweet-ops");
                urlConnection.setRequestProperty("Authorization", authorizationHeader);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                if(urlConnection.getResponseCode() != 200 && urlConnection.getResponseCode() != 404) {
                    success = false;
                    break;
                }
            } catch (MalformedURLException mue) {
                throw new RuntimeException(mue);
            } catch (Exception e) {
                // all other exceptions
                throw new RuntimeException(e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                    urlConnection = null;
                }
            }
        }
        return success;
    }

    @Override
    public boolean postTweet(String tweetContents) {
        return false;
    }

    @Override
    public boolean searchTweets(String searchString) {
        return false;
    }
}
