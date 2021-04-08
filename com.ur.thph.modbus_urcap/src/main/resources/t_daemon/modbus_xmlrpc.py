#!/usr/bin/env python

import sys
import socket
import struct
import time
import logging as Logger
import os, sys
currentdir = os.path.dirname(os.path.realpath(__file__))
parentdir = os.path.dirname(currentdir)
sys.path.append(parentdir)
import minimalmodbus as modbus
import serial

from SimpleXMLRPCServer import SimpleXMLRPCServer
from SocketServer import ThreadingMixIn

isShowing = False
LOCALHOST = "127.0.0.1"

instrument = None 
value = ""

def reachable():
  return True

def init_modbus_communication(slaveaddress):
  global instrument
  instrument = modbus.Instrument('/dev/ttyTool',slaveaddress)
  return True

def tool_modbus_write(register_address, data):
  try:
    global instrument
    instrument.write_register(register_address,data,0)
  except Exception:
    Logger.error("Error in modbus write method", exc_info=True)
    return "Modbus failed writing"
  return "Succesfully executed!"

def tool_modbus_read(register_address):
  global value
  try:
    global instrument
    value = int(instrument.read_register(register_address,0))
  except Exception:
    Logger.error("Error in modbus read method", exc_info=True)
    value = "Modbus falied reading"
  return value


class MultithreadedSimpleXMLRPCServer(ThreadingMixIn, SimpleXMLRPCServer):
    pass


# Connection related functions
server = MultithreadedSimpleXMLRPCServer((LOCALHOST, 40408))
server.RequestHandlerClass.protocol_version = "HTTP/1.1"
print "Listening on port 40408..."

server.register_function(reachable,"reachable")
server.register_function(init_modbus_communication,"init_modbus_communication")
server.register_function(tool_modbus_read,"tool_modbus_read")
server.register_function(tool_modbus_write,"tool_modbus_write")

server.serve_forever()

