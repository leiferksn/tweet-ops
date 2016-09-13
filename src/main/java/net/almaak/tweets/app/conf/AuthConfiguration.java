package net.almaak.tweets.app.conf;

import java.io.IOException;
import java.util.Properties;

/**
 * The class holds OAuth configuration data - keys and secrete.
 *
 * Created by leiferksn on 9/13/16.
 */
public class AuthConfiguration {

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    private static AuthConfiguration instance = null;

    public static AuthConfiguration getInstance() {
        if(instance == null) {
            instance = new AuthConfiguration();
        }
        return instance;
    }

    private AuthConfiguration() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("auth.properties"));
        } catch (IOException ioe) {
             throw new RuntimeException("Authorization properties can't be loaded!");
        }

        this.consumerKey = properties.getProperty("CONSUMER_KEY");
        this.consumerSecret = properties.getProperty("CONSUMER_SECRET");
        this.accessToken = properties.getProperty("ACCESS_TOKEN");
        this.accessTokenSecret = properties.getProperty("ACCESS_TOKEN_SECRET");
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }
}
