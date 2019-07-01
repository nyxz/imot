web: java -Dserver.port=$PORT $JAVA_OPTS -jar build/libs/imot-0.0.1-SNAPSHOT.jar
worker: curl -X POST https://imot.herokuapp.com/scrape-notify
