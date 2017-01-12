import { Component, OnInit } from '@angular/core';
import { HTTPService } from '../../../services/httpService.component';
import { SocketService } from '../../../services/socketService.component';

@Component({
  selector: 'users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  providers: [ HTTPService, SocketService ]
})

export class UsersComponent implements OnInit  {

    users = [];
    myUsername = "";
    errorMessage = "";
    constructor(private HTTPService: HTTPService, private SocketService:SocketService) { }

    ngOnInit() { 
       /* this.HTTPService.post('/getUsers', {}).subscribe(
            data => this.users.push(data),
             error =>  this.errorMessage = <any>error);
        console.log(this.errorMessage);*/
        this.myUsername = "mycoco";
        this.users = ["yousseef", "momo", "koko","yousseef", "momo", "koko","yousseef", "momo", "koko"];
        
         this.SocketService.getSocket().emit('setUsername', {
            username: this.myUsername
        });
    }
}