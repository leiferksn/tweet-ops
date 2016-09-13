package net.almaak.tweets.service;

import java.util.List;

/**
 * Created by leiferksn on 9/4/16.
 */
public interface TwitterOperationsService {

    boolean deleteTweets(List<String> tweetIds);
    boolean postTweet(String tweetContents);
    boolean searchTweets(String searchString);

}
