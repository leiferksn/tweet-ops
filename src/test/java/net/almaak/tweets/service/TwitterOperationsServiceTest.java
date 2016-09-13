package net.almaak.tweets.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by leiferksn on 9/13/16.
 */

public class TwitterOperationsServiceTest {

    private TwitterOperationsService twitterOperationsService;
    private String listOfTweetIds = "2344217599,2342342576,2341903523";

    @Before
    public void setUp(){
        twitterOperationsService = new TwitterOperationsServiceImpl();
    }

    @Test
    public void testShouldDeleteTweet() {
        List<String> tweetIds = Arrays.asList(listOfTweetIds.split(","));
        Assert.assertTrue(twitterOperationsService.deleteTweets(tweetIds));
    }
}
