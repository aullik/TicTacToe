var socket = require('../node_modules/socket.io');
var express = require('../node_modules/express');
var app = express();

var server = require('http').createServer(app);
var io = socket.listen(server);
var port = process.env.PORT || 3000;
var users = {};
var chatUsers = {};
var connectedUser = {};
var userOnTalk = {};

server.listen(port, function () {
    console.log('Server listening at port %d', port);
});

io.on('connection', function (socket) {
    var username = "";
    socket.on('setUsername', function (data) {
        users[data.username] = socket.id
        connectedUser[data.username] = true;
        userOnTalk[data.username] = false;
        username = data.username;
        console.log(username + " has connected");
    });
    socket.on('move', function (data) {
        if (users[data.toUsername] !== undefined && connectedUser[data.toUsername] == true) {
            console.log('user ' + data.fromUsername + ' has move to  ' + data.toUsername)
            io.sockets.connected[users[data.toUsername]].emit('move', {
                fromUsername: data.fromUsername,
                toUsername: data.toUsername,
                move: data.move,
                status: data.status,
                win: data.win
            });
        }
    });

    socket.on('disconnect', function () {
        if (users[username] !== undefined) {
            connectedUser[username] = false;
            setTimeout(function () {
                if (!connectedUser[username]) {
                    delete users[username];
                    console.log(username + " is disconnected");
                }
            }, 15000); //15s
        }
    });

});