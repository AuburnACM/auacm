# pylint: disable=I0011,C0103,C0202,W0702
'''
Handles the websockets for the site.
'''
from json import loads
from tornado.websocket import WebSocketHandler
from app.modules import app

def make_flasknado():
    ''' Creates a new class instance of Flasknado

    In a sense, this is kind of a hack. The problem is that the way Tornado
    works, it requires sending a WebSocketHandler class rather than an instance
    of that class. This means that in order to have separate instances, we must
    dynamically create different classes at runtime. This function simply
    returns a Flasknado class with an empty set of clients.
    '''

    values = dict(Flasknado.__dict__)
    values['clients'] = []  # give the new instance a new set of clients
    return type('Flasknado', Flasknado.__bases__, values)

class Flasknado(WebSocketHandler):
    ''' A class for handling sockets

    This class acts as a singleton that contains all of the websocket clients
    that are currently connected to the server.
    '''

    # Client connection pool
    clients = []

    # Callback dictionary for receiving messages
    callbacks = dict()

    @classmethod
    def emit(cls, event_type, data):
        '''
        Send a message to all clients currently connected

        :param event_type: the name of the message to send
        :param data: the dictionary of data to send
        :return: None
        '''
        for client in cls.clients:
            client.write_message(Flasknado._message(event_type, data))

    @classmethod
    def send(cls, conn, event_type, data):
        '''
        Send a message directly to a specific client

        :param conn: the Flasknado instance to send to
        :param event_type: the name of the event type
        :param data: the dictionary of data to pass
        :return: None
        '''
        conn.write_message(Flasknado._message(event_type, data))

    @classmethod
    def on(self, message):
        '''
        Decorator to register a callback function when receiving a message
        from the client.

        :param message: the name of the message to register the callback for
        '''
        def wrapped(function):
            '''Add message to callbacks if it does not exist.'''
            if message not in self.callbacks:
                self.callbacks[message] = function
        return wrapped

    def open(self):
        '''Add a client to the connection pool'''
        Flasknado.clients.append(self)

    def on_close(self):
        '''Remove a client from the connection pool'''
        Flasknado.clients.remove(self)

    def on_message(self, message):
        '''
        Recieve a message from the client and call the registered callback, if
        any

        :param message: The message name the callback is registered under
        '''
        try:
            message = loads(message)
            if message['eventType'] in self.callbacks:
                self.callbacks[message['eventType']](self)
        except:
            app.logger.error('Could not parse JSON from socket message: ' +
                             message)

    def data_received(self, data):
        pass

    @staticmethod
    def _message(event, data):
        '''Helper method to wrap a message for sending'''
        return {
            'eventType': event,
            'data': data
        }
