import {Component} from '@angular/core';

@Component({
    selector: 'home',
    templateUrl: 'assets/app/home.component.html',
    styleUrls: ['assets/app/home.component.css'],
})

export class HomeComponent {
    // users: any;
    // private WEB_SOCKET_SWF_LOCATION = '/javascript/WebSocketMain.swf';
   // private socket: any = null;
    // private username: any;
    constructor() {
       // this.socket = new WebSocket('ws://' + window.location.host + '/socket/');
       // this.socket.onopen = this.socketOpen(event);
    }
    public socketOpen(event: any) {
       // this.socket.send(JSON.stringify({msgType:'userStatus', value: {}}));
    }
}
