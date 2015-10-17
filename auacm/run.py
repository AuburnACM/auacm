#!flask/bin/python
from app import app, socketio

# app.run(debug=True)
socketio.run(app)
