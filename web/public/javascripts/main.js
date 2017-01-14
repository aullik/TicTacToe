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


$(document).ready(function () {
    /*socket.emit('setUsername', {
        username: username
    });*/
    $('.loginmodal-submit').click(function () {
        var data = $(this).parent().parent().serializeObject();
        $.ajax({
            url: $(this).parent().parent().attr('action'), // php script to retern json encoded string
            data: JSON.stringify(data),  // serialized data to send on server
            dataType: 'json', // set recieving type - JSON in case of a question
            type: 'POST', // set sending HTTP Request type
            contentType: 'application/json',
            async: false,
            success: function (data) { // callback method for further manipulations
                console.log(data)

            },
            error: function (data) { // if error occured

            }
        });
    });

    $('#callButton').click(function () {
        var otherUser = $(this).parent().children(':last-child').val();
        socket.emit('call', {
            fromUsername: username,
            toUsername: otherUser
        });
    });
    $('.callDeny').click(function () {
        $('#incomingCallModal').modal('hide');
        var otherUser = $('#otherUserModal').val();
        socket.emit('denyCall', {
            fromUsername: username,
            toUsername: otherUser
        });
    });
    $('.callAccept').click(function () {
        $('#incomingCallModal').modal('hide');
        var otherUser = $('#otherUserModal').val();
        //emit call accepted
       /*$.ajax({
            method: "POST",
            url: "http://localhost:9001/startGame/" + otherUser,
            success: function (output) {
                // output = $.parseJSON(output)
                console.log(output);
                socket.emit('acceptCall', {
                    fromUsername: username,
                    toUsername: otherUser
                });
            }
        });*/
    });
});

