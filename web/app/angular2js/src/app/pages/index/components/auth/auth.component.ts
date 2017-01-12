import { Component, OnInit } from '@angular/core';
import { HTTPService } from '../../../../services/httpService.component';

@Component({
    selector: 'my-auth',
  templateUrl: './my-auth.component.html',
  styleUrls: ['./my-auth.component.css'],
  providers: [ HTTPService ]
})
export class AuthComponent implements OnInit {
    loginModel = {
        email: "",
        password:"",
        errorMessage : "",
        loading : false
    };
    signUpModel = {
        email: "",
        username:"",
        password:"",
        errorMessage : "",
        loading : false
    };
    mode = 'Observable';
    returnUrl: string;
    constructor(private HTTPService: HTTPService) { }

    ngOnInit() {
        console.log('init')
     }

    submitLogin(){
        this.loginModel.loading = true;
        this.HTTPService.post('/login', {email: this.loginModel.email, passwword: this.loginModel.password})
                     .subscribe(
                       error =>  this.loginModel.errorMessage = <any>error);
        this.loginModel.loading = false;
        console.log(this.loginModel);
    }
    submitSignup(){
        this.signUpModel.loading = true;
        this.HTTPService.post('/signup', {email: this.signUpModel.email,
            username: this.signUpModel.username, 
        password: this.signUpModel.password})
                     .subscribe(
                       error =>  this.loginModel.errorMessage = <any>error);
        this.signUpModel.loading = false;
        console.log(this.signUpModel);
    }
}