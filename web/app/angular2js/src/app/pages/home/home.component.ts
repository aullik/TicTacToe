import { Component, OnInit } from '@angular/core';
import { HTTPService } from '../../services/httpService.component';
import { SocketService } from '../../services/socketService.component';

@Component({
    selector: 'home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css'],
  providers: [ HTTPService, SocketService ]
})
export class HomeComponent implements OnInit {

    constructor() { }

    ngOnInit() { 
       
    }
}



