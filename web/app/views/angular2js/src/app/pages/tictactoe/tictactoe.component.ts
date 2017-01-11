import { Component, OnInit, ElementRef  } from '@angular/core';
@Component({
    selector: 'tictactoe',
  templateUrl: './tictactoe.component.html',
  styleUrls: ['./tictactoe.component.css']
})
export class TictactoeComponent implements OnInit {
    constructor(private elementRef:ElementRef) { }

    ngOnInit() { }
    ngAfterViewInit() {
        var s = document.createElement("script");
        s.type = "text/javascript";
        s.src = "/play.js";
        this.elementRef.nativeElement.appendChild(s);
    }
}
