//var socket = io.connect('http://' + window.location.hostname + ':3000');

$(document).ready(function () {
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
});


$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};