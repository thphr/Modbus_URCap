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

def set_modbus_tool_communication(enabled,baud_rate,parity,stop_bits,rx_idle_chars,tx_idle_chars):
  global value
  value = "set_modbus_tool_communication("+str(enabled)+str(baud_rate)+str(parity)+str(stop_bits)+str(rx_idle_chars)+str(tx_idle_chars)+")"
  return value

def get_modbus_tool_communication():
  global value
  return value

def init_modbus_communication(port,slaveaddress):
  global instrument
  instrument = modbus.Instrument(port,slaveaddress)
  return True

def tool_modbus_write(register_address, data, number_of_decimals):
  try:
    instrument.write_register(register_address,data,number_of_decimals)
  except Exception:
    Logger.error("Error in modbus write method", exc_info=True)
    return "Modbus failed writing"
  return "Succesfully executed!"

def tool_modbus_read(register_address,number_of_decimals):
  try:
    value = str(instrument.read_register(register_address,number_of_decimals))
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

server.register_function(set_modbus_tool_communication,"set_modbus_tool_communication")
server.register_function(get_modbus_tool_communication,"get_modbus_tool_communication")
server.register_function(init_modbus_communication,"init_modbus_communication")
server.register_function(tool_modbus_read,"tool_modbus_read")
server.register_function(tool_modbus_write,"tool_modbus_write")

server.serve_forever()

