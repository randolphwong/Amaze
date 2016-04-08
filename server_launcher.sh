#!/bin/sh

cd core/build/classes/main/

echo "Starting server... "
echo "If there's not exception, then the server has started."
echo "**Note** Remember to change the server IP address in AmazeClient accordingly."

java com.mygdx.amaze.networking.ServerApp
