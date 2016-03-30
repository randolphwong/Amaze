@echo off
CD core\build\classes\main\

ECHO Starting server... 
ECHO If there's not exception, then the server has started.

java com.mygdx.amaze.networking.ServerApp
