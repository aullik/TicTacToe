import { Component, OnInit } from '@angular/core';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';

@Component({
    moduleId: module.id,
    selector: '<my-index></my-index>',
    templateUrl: 'my-index.html'
})
export class MyIndexComponent implements OnInit {
    constructor() { }

    ngOnInit() { }
}