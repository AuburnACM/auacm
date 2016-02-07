#!flask/bin/python
import eventlet
eventlet.monkey_patch(os=True, time=True)
from app import app, socketio
from subprocess import Popen

# app.run(debug=True)
socketio.run(app)
