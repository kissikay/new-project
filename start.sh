#!/bin/bash

# Terminate on any error
set -e

echo "Starting dbus service..."
dbus-uuidgen --ensure

echo "Starting virtual frame buffer Xvfb on display :99..."
Xvfb :99 -screen 0 1024x768x16 &
sleep 2

# Export DISPLAY variable
export DISPLAY=:99

echo "Starting Openbox window manager..."
openbox-session &
sleep 1

echo "Starting Java application..."
java -jar /app/academic-report-system.jar &

echo "Starting VNC server on port 5900..."
x11vnc -display :99 -forever -shared -nopw -rfbport 5900 -listen 127.0.0.1 &
sleep 2

PORT_TO_USE=${PORT:-10000}
echo "Starting websockify serving noVNC on port $PORT_TO_USE..."
exec websockify --web /usr/share/novnc $PORT_TO_USE 127.0.0.1:5900
