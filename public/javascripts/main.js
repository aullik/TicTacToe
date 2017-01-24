toastr.options = {
    "closeButton": false,
    "debug": false,
    "newestOnTop": false,
    "progressBar": false,
    "positionClass": "toast-bottom-center",
    "preventDuplicates": false,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "1000",
    "timeOut": "5000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
}

// Let the library know where WebSocketMain.swf is:
WEB_SOCKET_SWF_LOCATION = "/javascript/WebSocketMain.swf";

var socket = new WebSocket("wss://" + window.location.host + "/socket/");

var username = undefined;
var users;

this.socket.onopen = function onOpen(event) {
    console.log('Socket opened');;
    if(username == undefined){
        socket.send(JSON.stringify({msgType:'userStatus', value: {}}));
    }
}

this.socket.onerror = function onError(event) {
    console.error("Error: " + JSON.stringify(event.reason));
}

this.socket.onclose = function onClose(event) {
    console.log("socket close and opened")
    socket = new WebSocket("wss://" + window.location.host + "/socket/");
}
this.socket.onmessage = function socketOnMessage(event){
    var msg = JSON.parse(event.data);
    console.log(msg)
    switch (msg.msgType) {
        case "userStatusRet":
            userHandleStatusRet(msg.value);
            break;
        case "userLoggedIn":
            handleUserLoggedIn(msg.value);
            break;
        case 'userLoggedOut':
            handleUserLoggedOut(msg.value);
            break;
        case 'gameRequested':
            handleRequestGame(msg.value);
            break;
        case 'askForGameRet':
            handleCallAskForGameRet(msg.value);
            break;
        case 'startGame':
            handleStartGame(msg.value);
            break;
        default:
            console.warn("Could not handle this message: " + msg);
    }

};
function userHandleStatusRet(data) {
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
    if(data.name && data.token){
        $('.callRequestTitle').html("call to start a game with " + data.name);
        $('#incomingCallModal').modal({backdrop: 'static', keyboard: false});
        $('#otherUserModal').val(data.token);
    }
}
function handleCallAskForGameRet(data) {
    if(data.accept !== undefined && data.accept == false){
        $('#waitingModal').modal('hide');
        toastr.info('Game request denied');
    }
}
function handleStartGame(data) {
    window.location.href = 'https://' + window.location.host + '/tictactoe';
}

$(document).on('click','#callButton', function (){
    var otherUser = $(this).parent().children(':last-child').val();
    var user = users.find(x => x.token == otherUser);
    if(user){
        console.log("ask for game to: ")
        console.log(user)
        socket.send(JSON.stringify({
            msgType:'askForGame',
            value : user
        }));
        toastr.success('call has been sent');
        $('#waitingModal').modal({backdrop: 'static', keyboard: false});
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
    console.log('gameRequestedRet: ');
    console.log(user);
    socket.send(JSON.stringify({
        msgType:'gameRequestedRet',
        value : user
    }));
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
    data.push('<img src="http://placehold.it/50/'+ randomColor +'/fff&text='+user.charAt(0).toUpperCase()+'" alt="User Avatar" class="img-circle" >');
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
