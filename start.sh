#!/bin/bash

# Do NOT use set -e — background processes like openbox exit non-zero
# and would kill the whole script before websockify starts.

echo "Starting dbus service..."
dbus-uuidgen --ensure || true

echo "Starting virtual frame buffer Xvfb on display :99..."
Xvfb :99 -screen 0 1024x768x16 &
sleep 3

# Export DISPLAY variable
export DISPLAY=:99

echo "Starting Openbox window manager..."
openbox-session &
sleep 2

echo "Starting Java application..."
java -jar /app/academic-report-system.jar &
sleep 2

echo "Starting VNC server on port 5900..."
x11vnc -display :99 -forever -shared -nopw -rfbport 5900 -listen 127.0.0.1 &
sleep 2

PORT_TO_USE=${PORT:-10000}
echo "Starting websockify on 0.0.0.0:$PORT_TO_USE -> 127.0.0.1:5900..."
exec websockify --listen-host 0.0.0.0 --web /usr/share/novnc $PORT_TO_USE 127.0.0.1:5900
