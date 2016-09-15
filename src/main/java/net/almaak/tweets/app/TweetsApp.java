package net.almaak.tweets.app;

import net.almaak.tweets.app.conf.AllowedOperation;
import net.almaak.tweets.service.TwitterOperationsServiceImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application entry point
 *
 * Created by leiferksn on 9/1/16.
 */

public class TweetsApp {

    public static void main(final String[] args ) {

        /**
         * operation = d  (at the moment only delete is allowed)
         * tweet_ids = path to txt file containing a list of tweet ids
         *
         */

        Map<String, String> parameters = convertParametetsToHashMap(args);

        if(!checkIfParametersValid(parameters)) {
            System.out.println("Parameters not valid. Check your input");
            System.exit(0);
        }

        String tweetsIDsFilePath = parameters.get("tweet_ids");
        File file = new File(tweetsIDsFilePath);

        AllowedOperation allowedOperation = AllowedOperation.getOperationByString(parameters.get("operation"));
        if(allowedOperation.equals(AllowedOperation.DELETE)){
            boolean ok = deleteTweets(file);
            if(!ok){
                System.out.println("Operation interrupted! Check logs.");
            } else {
                System.out.println("Tweets deleted successfully.");
            }
        } else {
            System.out.println("Operation not yet implemented: " + allowedOperation);
        }
    }

    private static boolean deleteTweets(File file){
        List<String> tweetIDs = new ArrayList<String>();
        try {
            tweetIDs = readTweetIDs(file);
        } catch (FileNotFoundException fnfe) {
            System.out.println("File not found. Check path -> " + file);
            System.exit(0);
        } catch (IOException ioe) {
            System.out.println("Can't read from file. Check permissions.");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Error reading tweet IDs from file: " + e.getMessage() + e.getStackTrace());
            System.exit(0);
        }

        if(tweetIDs != null) {
            TwitterOperationsServiceImpl twitterOperationsService = new TwitterOperationsServiceImpl();
            return twitterOperationsService.deleteTweets(tweetIDs);
        } else {
            System.out.println("List of tweet IDs is empty");
            return false;
        }
    }

    private static boolean checkIfParametersValid(final Map<String, String> parameters) {
        if(!parameters.containsKey("operation")) {
            return false;
        }

        if(AllowedOperation.getOperationByString(parameters.get("operation")) == null) {
            System.out.println("Invalid operation: " + parameters.get("operation"));
            System.out.println("Only the operations: [ " + AllowedOperation.listValues() + " ] are allowed at present.");
            return false;
        }

        if(parameters.get("operation").equalsIgnoreCase("d") && !parameters.containsKey("tweet_ids")){
            System.out.println("Missing parameter 'tweets_ids'.");
            return false;
        }

        return true;
    }

    private static List<String> readTweetIDs(final File file) throws FileNotFoundException, IOException {
        List<String> tweetIDs = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line = null;
        while((line = reader.readLine()) != null) {
            tweetIDs.add(line);
        }
        return tweetIDs;
    }

    private static Map<String, String> convertParametetsToHashMap(final String[] parameters){
        Map<String, String> parameterMap = new HashMap<String, String>();
        for(String p : parameters){
            String[] singleParam = p.split("=");
            if(singleParam[0] != null) {
                parameterMap.put(singleParam[0], singleParam[1]);
            }
        }
        return parameterMap;
    }

}
