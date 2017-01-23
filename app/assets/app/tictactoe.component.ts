import {Component, ElementRef } from '@angular/core';

@Component({
    selector: 'tictactoe',
    templateUrl: 'assets/app/tictactoe.component.html',
    styleUrls: ['assets/app/tictactoe.component.css'],
})

export class TictactoeComponent {
    constructor(private elementRef: ElementRef) { }
    public ngAfterViewInit() {
        let s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = 'assets/javascripts/play.js';
        this.elementRef.nativeElement.appendChild(s);
    }
}
