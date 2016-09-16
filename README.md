## tweet-ops
Basically trying out operations available in the Twitter API in a JAVA console application. 

#### First stage...

... is to implement a delete service that grabs a list of tweet IDs and deletes the tweets.

#### Environment, Build, Execution and Configuration

Development <b>environment</b> is Java 1.6. 

Gradle is used for <b>building</b>. 

<b>OAuth credentils</b> are stored in a property file, which are loaded into a configuration singleton that is used throughout the whole runtime. 

<b>Execution:</b> 

To <b>DELETE</b> tweets:

java -jar tweet-ops-1.0-SNAPSHOT.jar operation=d tweet_ids=/tmp/tweets.txt


