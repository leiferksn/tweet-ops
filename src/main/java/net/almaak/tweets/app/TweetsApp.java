package net.almaak.tweets.app;

import java.util.HashMap;
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
         * tweet_ids = file containing a list of tweet ids
         *
         */

        Map<String, String> parameters = convertParametetsToHashMap(args);

        if(!checkIfParametersValid(parameters)) {
            System.out.println("Parameters not valid. Check your input");
            System.exit(0);
        }
    }

    private static boolean checkIfParametersValid(final Map<String, String> parameters) {
        if(!parameters.containsKey("operation")) {
            return false;
        }

        if (!parameters.get("operation").equalsIgnoreCase("d")) {
            return false;
        }

        if(parameters.get("operation").equalsIgnoreCase("d") && !parameters.containsKey("tweet_ids")){
            return false;
        }

        return true;
    }

    private static boolean checkIfParametersValid(final String[] parameters) {
        for(String p : parameters) {
            if(!p.contains("=")){
                // syntax of parameters is wrong
                return false;
            }
            String[] singleParam = p.split("=");
            if (singleParam[0].equalsIgnoreCase("operation")
                    && (singleParam[1] == "" || !singleParam[1].equalsIgnoreCase("d"))) {
                // operation is not properly defined
                return false;
            }
        }
        return true;
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
