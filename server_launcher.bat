@echo off
CD core\build\classes\main\

ECHO Starting server... 
ECHO **Note** Remember to change the server IP address in AmazeClient accordingly.

java com.mygdx.amaze.networking.ServerApp
