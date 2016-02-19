function Socket(url) {
    this.ws = new WebSocket(url);
    this.events = {};
    var parent = this;
    this.on = function(event, func) {
        parent.events[event] = func;
    };
    this.ws.onmessage = function(event) {
        var data = JSON.parse(event.data);
        if (data.eventType in parent.events) {
            parent.events[data.eventType](data.data);
        }
    };
    this.send = function(type, data) {
        var string = JSON.stringify({'eventType': type, 'data': data});
        parent.ws.send(string);
    };
}
