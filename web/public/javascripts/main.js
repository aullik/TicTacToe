

// Let the library know where WebSocketMain.swf is:
WEB_SOCKET_SWF_LOCATION = "/javascript/WebSocketMain.swf";

// Write your code in the same way as for native WebSocket:
//FIXME fix email
var socket = new WebSocket("ws://" + window.location.host + "/socket/");

var username;
var users;
$(document).ready(function () {
    output = {
        name: "alice",
        token: "alicetoken",
        users: [{
            name: "spieler1",
            token: "asdf"
        },{
            name: "spieler2",
            token: "fjosid"
        },
            {
                name: "spieler1",
                token: "asdf"
            },{
                name: "spieler2",
                token: "fjosid"
            }]
    }
    username = output.name;
    token = output.token;
    users = output.users;
    if(output.users){
        var usersData = [];
        for (var i = 0; i < output.users.length; i++){
            usersData.push(userBlock(output.users[i].name, output.users[i].token))
        }
        $('.allUsers').append(usersData.join(''));
    }
});
/*
socket.onopen(function () {
    socket.send({
        msgType:'getStatus'
    });
    socket.onmessage(socketOnMessage);
});*/
function socketOnMessage(event){
    var msg = JSON.parse(event.data);
    switch (msg.msgType) {
        case "getStatusResponse":
            this.handleStatusResponse(msg.value);
            break;
        case "userLoggedIn":
            this.handleUserLoggedIn(msg.value);
            break;
        case 'userLoggedOut':
            this.handleUserLoggedOut(msg.value);
            break;
        case 'requestGame':
            this.handleRequestGame(msg.value);
            break;
        case 'askForGameAck':
            this.handleCallAskForGameAck(msg.value);
            break;
        case 'startGame':
            this.handleStartGame(msg.value);
            break;
        default:
            console.warn("Could not handle this message: " + msg);
    }

};
function handleStatusResponse(data) {
    data = $.parseJSON(data);
    username = data.name;
    token = data.token;
    users = data.users;
    if(data.users){
        var usersData = [];
        for (var i = 0; i < data.users.length; i++){
            usersData.push(userBlock(data.users[i].name, data.users[i].token))
        }
        $('.allUsers').append(usersData.join(''));
    }
}
function handleUserLoggedIn(data) {
    if(data.name && data.token){
        $('.allUsers').append(userBlock(data.name, data.token));
    }
}
function handleUserLoggedOut(data) {
    if(data.name && data.token){
        $('#'+data.token).remove();
    }
}
function handleRequestGame(data) {
    if(data.user && data.token){
        $('.callRequestTitle').html("call to start a game with " + data.name);
        $('#incomingCallModal').modal({backdrop: 'static', keyboard: false});
        $('#otherUserModal').val(data.token);
    }
}
function handleCallAskForGameAck(data) {
    if(data.accept !== undefined && data.accept == false){
        $('#waitingModal').modal('hide');
        toastr.info('Game request denied');
    }
}
function handleStartGame(data) {
    window.location.href = 'http://' + window.location.host + '/tictactoe';
}

$(document).on('click','#callButton', function (){
    var otherUser = $(this).parent().children(':last-child').val();
    var user = users.find(x => x.token == otherUser);
    if(user){
        $('#waitingModal').modal({backdrop: 'static', keyboard: false});
        console.log("ask for game to: ")
        console.log(user)
        socket.send({
            msgType:'askForGame',
            value : user
        })
    }
});
$(document).on('click','.callDeny', function (){
    hideRequestGameModalAndSendResp(false)
});
$(document).on('click','.callAccept', function (){
    hideRequestGameModalAndSendResp(true)
});
function hideRequestGameModalAndSendResp(accept) {
    $('#incomingCallModal').modal('hide');
    var otherUser = $('#otherUserModal').val();
    var user = users.find(x => x.token == otherUser);
    user.accept = accept;
    console.log('responseGame: ');
    console.log(user);
    socket.send({
        msgType:'responseGame',
        value : user
    })
}
function getRandomColor() {
    var letters = '0123456789ABCDEF';
    var color = '';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}
function userBlock(user,token) {
    var data = [];
    var randomColor = getRandomColor();
    data.push('<div class="thumbnail users" id="'+token+'">');
    data.push('<div class="userPic">');
    data.push('<img src="http://placehold.it/50/'+ randomColor +'/fff&text='+user.charAt(0)+'" alt="User Avatar" class="img-circle" >');
    data.push('</div>');
    data.push('<div class="caption" style="display: flex;padding-right: 0px;">');
    data.push('<p style="margin: auto;margin-left: 0;"><b>'+user+'</b></p>');
    data.push('<button id="callButton" style="float: right;font-size: 24px;"><i class="fa fa-4 fa-gamepad" aria-hidden="true"></i></button>');
    data.push('<input type="hidden" id="imgColor" value="'+randomColor+'">');
    data.push('<input type="hidden" id="otherUser" value="'+token+'">');
    data.push('</div>');
    data.push('</div>');
    return data.join('');
}
