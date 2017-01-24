import {Component} from '@angular/core';
import {MdSnackBar} from '@angular/material';
import {MdDialog, MdDialogRef} from '@angular/material';
import {SpinningModal}     from './spinningModal.component';
import {CallModal}     from './callModal.component';

@Component({
    selector: 'users',
    templateUrl: 'assets/app/users.component.html',
    styleUrls: ['assets/app/users.component.css'],
})
export class UsersComponent {
    public title = 'app works!';
    public users: any[];
    public backgrounds: any = {};
    private WEB_SOCKET_SWF_LOCATION = '/javascript/WebSocketMain.swf';
    private socket: any = null;
    private username: any;
    private token: any;
    private dialogRef: any;
    private firstOpen: boolean = true;

    constructor(public snackBar: MdSnackBar, public dialog: MdDialog) {
        this.socket = new WebSocket('wss://' + window.location.host + '/socket/');
        this.socket.onmessage = this.socketMessage.bind(this);
        this.socket.onopen = this.socketOpen.bind(this);
        this.socket.onerror = this.socketError.bind(this);
        this.socket.onclose = this.socketClosed.bind(this);
    }
    public socketOpen(event: any) {
        if (this.firstOpen) {
            this.socket.send(JSON.stringify({msgType : 'userStatus', value : {}}));
            this.firstOpen = false;
        }
    }
    public socketError(event: any) {
        alert('error' + event);
    }
    public socketClosed(event: any) {
        this.socket = new WebSocket('wss://' + window.location.host + '/socket/');
    }
    public socketMessage(event: any) {
        let msg = JSON.parse(event.data);
        switch (msg.msgType) {
            case 'userStatusRet':
                this.userHandleStatusRet(msg.value);
                break;
            case 'userLoggedIn':
                this.handleUserLoggedIn(msg.value);
                break;
            case 'userLoggedOut':
                this.handleUserLoggedOut(msg.value);
                break;
            case 'gameRequested':
               /// this.handleRequestGame(msg.value);
                break;
            case 'askForGameRet':
               // this.handleCallAskForGameRet(msg.value);
                break;
            case 'startGame':
               // this.handleStartGame(msg.value);
                break;
            default:
                console.warn('Could not handle this message: ' + msg);
        }
    }
    public userHandleStatusRet(data: any) {
        this.username = data.name;
        this.token = data.token;
        this.users = data.users;
        for (let user of this.users) {
            this.backgrounds[user.token] = 'http://placehold.it/50/' + this.getRandomColor()
                + '/fff&text=' + user.name.charAt(0).toUpperCase();
        }
    }
    public handleUserLoggedIn(data: any) {
        this.users.push(data);
    }
    public handleUserLoggedOut(data: any) {
        let index = this.users.findIndex(x => x.token === data.token);
        this.users.splice(index, 1);
    }
    public getRandomColor() {
        let letters = '0123456789ABCDEF';
        let color = '';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }

    public startGame(user: any) {
        this.socket.send(JSON.stringify({
            msgType: 'askForGame',
            value: user,
        }));
        this.snackBar.open('Anruf wurde geschickt', '', {
            duration: 2000,
        });
        this.dialogRef = this.dialog.open(SpinningModal, {disableClose: true});

    }

    public handleRequestGame(data: any) {
        let dialogRefCall = this.dialog.open(CallModal, {disableClose: true});
        dialogRefCall.componentInstance.user = data.name;
        dialogRefCall.afterClosed().subscribe(result => {
            switch (result) {
                case 'a':
                    this.acceptCall(data);
                    break;
                case 'd':
                    this.denyCall(data);
                    break;
                default:
                    console.warn('data not available ' + result);
            }
        });
    }
    public denyCall(user: any) {
        user.accept = false;
        this.socket.send(JSON.stringify({
            msgType: 'gameRequestedRet',
            value: user,
        }));
    }
    public acceptCall(user: any) {
        user.accept = true;
        this.socket.send(JSON.stringify({
            msgType: 'gameRequestedRet',
            value: user,
        }));
    }
    public handleCallAskForGameRet(data: any) {
        if (data.accept !== undefined && data.accept === false) {
            this.dialogRef.close();
            this.snackBar.open('Anruf wurde abgelehnt', '', {
                duration: 2000,
            });
        }
    }
    public handleStartGame(data: any) {
        window.location.href = 'https://' + window.location.host + '/tictactoe';
    }
}
