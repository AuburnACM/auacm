#!flask/bin/python
from app import app, socketio
from subprocess import Popen

# app.run(debug=True)
socketio.run(app)
