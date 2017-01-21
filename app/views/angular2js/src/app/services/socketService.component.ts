import { Injectable } from '@angular/core';

var io = require("../../../node_modules/socket.io-client/dist/socket.io.min.js");

@Injectable()
export class SocketService{
    socket = null;
    constructor() { 
        this.socket = io.connect( 'http://localhost:3000' );
    }
    getSocket(){
        return this.socket;
    }

}