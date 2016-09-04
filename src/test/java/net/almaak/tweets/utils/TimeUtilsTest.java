package net.almaak.tweets.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by leiferksn on 9/5/16.
 */
public class TimeUtilsTest {

    @Test
    public void shouldRetrieveTimeOffsetToServer() throws Exception {
        Long timeOffset = TimeUtils.retrieveTimeOffsetToServer();
        Assert.assertTrue(timeOffset != 0);
    }
}
