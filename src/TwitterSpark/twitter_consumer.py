
import time
import json
from pyspark import SparkContext
from pyspark.streaming import StreamingContext


# Our filter function:
def filter_tweets(tweet):
    json_tweet = json.loads(tweet)
    if json_tweet.has_key('lang'):  # When the lang key was not present it caused issues
        if json_tweet['lang'] == 'ar':
            return True  # filter() requires a Boolean value
    return False

#sc = SparkContext("local[2]", "Twitter Demo")
#ssc = StreamingContext(sc, 10)  # 10 is the batch interval in seconds
IP = "localhost"
Port = 5555
lines = ssc.socketTextStream(IP, Port)
print lines
#filter_tweets(lines)

#print lines.count()
#lines.foreachRDD(lambda rdd: rdd.filter(filter_tweets).coalesce(1).saveAsTextFile("./tweets/%f" % time.time()))

#ssc.start()
#ssc.awaitTermination()