#!/bin/sh

cd core/build/classes/main/

echo "Starting server... "
echo "If there's not exception, then the server has started."

java com.mygdx.amaze.networking.ServerApp
