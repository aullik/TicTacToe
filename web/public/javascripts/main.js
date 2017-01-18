

/*var socket = io.connect('http://' + window.location.hostname + ':3000');

socket.on('call', function (data) {
    $('.callRequestTitle').html("call to start a game from : " + data.fromUsername);
    $('#incomingCallModal').modal({backdrop: 'static', keyboard: false});
    $('#otherUserModal').val(data.fromUsername);
});
socket.on('denyCall', function (data) {
    alert(data + " deny")
});
socket.on('callAccepted', function (data) {
    window.location.href = 'http://localhost:9001/tictactoe';
});
socket.on('acceptCall', function (data) {
    console.log(data)
    socket.emit('callAccepted', {
        fromUsername: data.toUsername,
        toUsername: data.fromUsername
    });
    window.location.href = 'http://localhost:9001/tictactoe';
});*/

// Let the library know where WebSocketMain.swf is:
WEB_SOCKET_SWF_LOCATION = "/javascript/WebSocketMain.swf";

// Write your code in the same way as for native WebSocket:
//FIXME fix email
var socket = new WebSocket("ws://" + window.location.host + "/socket/");

socket.onmessage(function(event){
    var msg = JSON.parse(event.data);
    console.log(msg)
    switch (msg.msgType) {
        case "call":
            this.handleCall(msg.action);
            break;
        case 'denyCall':
            this.handleDenyCall(msg.action);
            break;
        case 'acceptCall':
            this.handleAcceptCall(msg.action);
            break;
        case 'callAccepted':
            this.handleCallAccepted(msg.action);
            break;
        case 'newUser':
            this.handleNewUser(msg.action);
            break;
        default:
            console.warn("Could not handle this message: " + msg);
    }

});
function handleNewUser(data) {
    if(data.name && data.token){
        $('.allUsers').append(userBlock(data.name, data.token));
    }
}
function handleCall(data) {
    var user = users.find(x => x.token == data);
    if(user){
        $('.callRequestTitle').html("call to start a game with " + user.name);
        $('#incomingCallModal').modal({backdrop: 'static', keyboard: false});
        $('#otherUserModal').val(user.token);
    }
}
function handleDenyCall(data) {
    alert("call denied")
}
function handleAcceptCall(data) {
    socket.send({
        msgType:'callAccepted',
        action : data
    });
    window.location.href = 'http://' + window.location.host + '/tictactoe';
}
function handleCallAccepted(data) {
    window.location.href = 'http://' + window.location.host + '/tictactoe';
}

$(document).on('click','#callButton', function (){
    var otherUser = $(this).parent().children(':last-child').val();
    socket.send({
        msgType:'call',
        action : otherUser
    })
});
$(document).on('click','.callDeny', function (){
    $('#incomingCallModal').modal('hide');
    var otherUser = $('#otherUserModal').val();
    socket.send({
        msgType:'denyCall',
        action : otherUser
    })
});
$(document).on('click','.callAccept', function (){
    $('#incomingCallModal').modal('hide');
    var otherUser = $('#otherUserModal').val();
    socket.send({
        msgType:'acceptCall',
        action : otherUser
    })
});

