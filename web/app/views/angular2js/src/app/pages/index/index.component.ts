import { Component, OnInit } from '@angular/core';
import { InfoComponent } from './components/info/info.component';
import { AuthComponent } from './components/auth/auth.component';

@Component({
    selector: 'my-index',
  templateUrl: './my-index.component.html',
  styleUrls: ['./my-index.component.css']
})
export class MyIndexComponent implements OnInit {
    constructor() { }

    ngOnInit() { }
}