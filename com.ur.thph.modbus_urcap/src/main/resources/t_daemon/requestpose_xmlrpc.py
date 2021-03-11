#!/usr/bin/env python

import sys
import socket
import struct
import time
import minimalmodbus as modbus
import serial 
import logging as Logger

from SimpleXMLRPCServer import SimpleXMLRPCServer
from SocketServer import ThreadingMixIn

isShowing = False
LOCALHOST = "127.0.0.1"

instrument = None 
value = ""

def init_modbus_communication(port,slaveaddress):
  global instrument
  instrument = modbus.Instrument(port,slaveaddress)
  return True

def tool_modbus_write(register_address, data):
  global instrument
  try:
    instrument.write_register(register_address,data,0)
  except Exception:
    Logger.error("Error in modbus write method", exc_info=True)
    return "Modbus failed writing"
  return "Succesfully executed!"

def tool_modbus_read(register_address):
  global instrument
  try:
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

server.register_function(init_modbus_communication,"init_modbus_communication")
server.register_function(tool_modbus_read,"tool_modbus_read")
server.register_function(tool_modbus_write,"tool_modbus_write")

server.serve_forever()

