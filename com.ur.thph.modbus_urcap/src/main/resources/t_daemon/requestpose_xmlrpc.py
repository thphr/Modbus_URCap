#!/usr/bin/env python

import sys
import socket
import struct
import time

from SimpleXMLRPCServer import SimpleXMLRPCServer
from SocketServer import ThreadingMixIn

isShowing = False
LOCALHOST = "127.0.0.1"

def showpopup():
  global isShowing
  isShowing = True
  return isShowing

def isEnabled():
  global isShowing
  return isShowing

def cancelpopup():
  global isShowing
  isShowing = False
  return isShowing



class MultithreadedSimpleXMLRPCServer(ThreadingMixIn, SimpleXMLRPCServer):
    pass


# Connection related functions
server = MultithreadedSimpleXMLRPCServer((LOCALHOST, 40405))
server.RequestHandlerClass.protocol_version = "HTTP/1.1"
print "Listening on port 40405..."

server.register_function(showpopup,"showpopup")
server.register_function(cancelpopup,"cancelpopup")
server.register_function(isEnabled,"isEnabled")


server.serve_forever()

