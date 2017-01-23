import {Component} from '@angular/core';
import { HTTPService } from './HTTPService.component';

@Component({
    selector: 'my-app',
    templateUrl: 'assets/app/app.component.html',
    styleUrls: ['assets/app/app.component.css'],
    providers: [ HTTPService ],
})
export class AppComponent {
    public title = 'app works!';
    constructor(private HTTPService: HTTPService) {}
    public redirect(data: string) {
        this.HTTPService.post('/selectFramework/' + data, {})
            .subscribe(
                error =>  console.warn(error),
                response => window.location.href = window.location.href);
    }
}
