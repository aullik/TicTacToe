import {Component} from '@angular/core';
import {BackToIndexModal}     from './backToIndexModal.component';
import {MdDialog} from '@angular/material';

@Component({
    selector: 'my-chat',
    templateUrl: 'assets/app/chat.component.html',
    styleUrls: ['assets/app/chat.component.css'],
})
export class ChatComponent {
    public data = {
        textToSend: '',
    };
    public messages: any[] = [];
    public user: any[];
    public otherUser: any[];
    private WEB_SOCKET_SWF_LOCATION = '/javascript/WebSocketMain.swf';
    private ticSocket: any = null;
    private myAvatarColor: string;
    private firstOpen: boolean = true;
    constructor(public dialog: MdDialog) {
        this.ticSocket = new WebSocket('wss://' + window.location.host + '/socket/');
        this.ticSocket.onmessage = this.ticSocketMessage.bind(this);
        this.ticSocket.onopen = this.ticSocketOpen.bind(this);
        this.ticSocket.onerror = this.ticSocketError.bind(this);
        this.ticSocket.onclose = this.ticSocketClosed.bind(this);
        this.myAvatarColor = this.getRandomColor();

    }
    public getRandomColor() {
        let letters = '0123456789ABCDEF';
        let color = '';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }
    public ticSocketOpen(event: any) {
        if (this.firstOpen) {
            this.ticSocket.send(JSON.stringify({
                msgType: 'gamePlayers',
                value: {},
            }));
            this.firstOpen = false;
        }
    }
    public ticSocketError(event: any) {
        alert('error' + event);
    }
    public ticSocketClosed(event: any) {
        this.ticSocket = new WebSocket('wss://' + window.location.host + '/socket/');
    }
    public ticSocketMessage(event: any) {
        let msg = JSON.parse(event.data);
        switch (msg.msgType) {
            case 'acceptMessage':
                this.handleAcceptMessage(msg.value);
                break;
            case 'gamePlayersRet':
                this.handleGamePlayersRet(msg.value);
                break;
            case 'returnToIndex':
                this.handleReturn(msg.value);
                break;
            default:
                console.warn('Could not handle this message: ' + msg);
        }
    }
    public handleReturn(data: any) {
        let dialogRefCall = this.dialog.open(BackToIndexModal, {disableClose: true});
    }
    public handleAcceptMessage(message: any) {
        message.user = this.otherUser;
        message.me = false;
        message.url = 'http://placehold.it/50/'
            + message.avatarColor + '/fff&text=' + message.user.name.charAt(0).toUpperCase();
        this.messages.push(message);
    }
    public handleGamePlayersRet(data: any) {
        this.user = data.me;
        this.otherUser = data.other;
    }
    public sendMessage() {
        let input = this.data.textToSend;
        if (input) {
            let timeNow = new Date();
            let data: any = {
                avatarColor: this.myAvatarColor,
                timestamp: timeNow.getHours() + ':' + timeNow.getMinutes(),
                body: input,
            };
            this.ticSocket.send(JSON.stringify({
                msgType: 'message',
                value: data,
            }));
            data.user = this.user;
            data.url = 'http://placehold.it/50/'
                + data.avatarColor + '/fff&text=' + data.user.name.charAt(0).toUpperCase();
            data.me = true;
            this.messages.push(data);
            this.data.textToSend = '';
        }
    }
}
