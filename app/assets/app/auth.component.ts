import {Component} from '@angular/core';
import { HTTPService } from './HTTPService.component';
import {MdSnackBar} from '@angular/material';

@Component({
    selector: 'my-auth',
    templateUrl: 'assets/app/auth.component.html',
    styleUrls: ['assets/app/auth.component.css'],
    providers: [ HTTPService ],
})
export class AuthComponent {
    public loginModel = {
        email : '',
        password : '',
        errorMessage : '',
        rememberMe: false,
        loading : false,
    };
    public signUpModel = {
        email : '',
        username : '',
        password : '',
        passwordRepetition: '',
        errorMessage : '',
        rememberMe: false,
        loading : false,
    };
    public mode = 'Observable';
    public returnUrl: string;
    constructor(private HTTPService: HTTPService, public snackBar: MdSnackBar) { }
    public submitLogin() {
        this.loginModel.loading = true;
        this.HTTPService.post('/login', JSON.stringify({email: this.loginModel.email,
            password: this.loginModel.password, rememberMe: this.loginModel.rememberMe}))
            .subscribe( error =>  this.loginModel.errorMessage = <any> error,
                        response => {
                            console.warn(JSON.stringify(response));
                            window.location.href = 'http://' + window.location.host + '/index'; });
        this.loginModel.loading = false;
        window.location.href = window.location.href;
    }
    public submitSignup() {
        this.signUpModel.loading = true;
        this.HTTPService.post('/signup', JSON.stringify({name: this.signUpModel.username,
                email: this.signUpModel.email,
            password: this.signUpModel.password,
            passwordRepetition: this.signUpModel.passwordRepetition,
            rememberMe: this.signUpModel.rememberMe}))
            .subscribe(
                error => {
                    this.loginModel.errorMessage = <any> error; this.snackBar.open('Fehler beim Registrieren, ' +
                    'bitte versuchen Sie es später',
                    '', {
                    duration: 2000,
                }); },
                response => this.snackBar.open('Wir haben Ihnen eine Email geschickt, ' +
                    'bitte schauen Sie Ihre Mailbox', '', {
                    duration: 2000,
                }));
        this.signUpModel.loading = false;
    }
}
