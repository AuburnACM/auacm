function Socket(url) {
    ws = new WebSocket(url);
    this.events = {};
    var parent = this;
    this.on = function(event, func) {
        parent.events[event] = func;
    };
    ws.onmessage = function(event) {
        var data = JSON.parse(event.data);
        if (data.eventType in parent.events) {
            parent.events[data.eventType](data.data);
        }
    };
}
