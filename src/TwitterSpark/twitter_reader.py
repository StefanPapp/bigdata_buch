import os
import tweepy
from tweepy import OAuthHandler
from tweepy import Stream
from tweepy.streaming import StreamListener
import socket
import json
 
consumer_key    = 'YsN071a3nnuu3NX9OFFDNB4zO'
consumer_secret = 'hmKHrkI3wx4EOZt1Nxxk8YBvBl19yYSMx27CZyYks6V4q0GATv'
access_token    = '14316485-EW8jCZQVNiYE4aSlbmdNYaktTvEiyexrCtXIIjxv6'
access_secret   = 'GmBsrDSqTrlIGjd61UmLmWqHGOk24K0BaezDO9lmDGeqX'
 

class TweetsListener(StreamListener):
 
    def __init__(self, csocket):
        self.client_socket = csocket
 
    def on_data(self, data):
        try:
            print(data.split('\n'))
            self.client_socket.send(data)
            return True
        except BaseException as e:
            print("Error on_data: %s" % str(e))
        return True
 
    def on_error(self, status):
        print(status)
        return True
 
def sendData(c_socket):
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_secret)
 
    twitter_stream = Stream(auth, TweetsListener(c_socket))
    twitter_stream.filter(track=['trump'])
 
if __name__ == "__main__":
    s = socket.socket()     # Create a socket object
    host = "localhost"      # Get local machine name
    port = 5555             # Reserve a port for your service.
    s.bind((host, port))    # Bind to the port
 
    print("Listening on port: %s" % str(port))
 
    s.listen(5)                 # Now wait for client connection.
    c, addr = s.accept()        # Establish connection with client.
 
    print( "Received request from: " + str( addr ) )
 
    sendData( c )