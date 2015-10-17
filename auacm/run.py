#!flask/bin/python
from app import app, socketio
from subprocess import Popen

app.run(debug=True)
# socketio.run(app)

# attempt at starting socket server
Popen(['gunicorn', '--worker-class', 'socketio.sgunicorn.GeventSocketIOWorker', 'module:app'])
