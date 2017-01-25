import {Component} from '@angular/core';

@Component({
    selector: 'back-to-index-modal',
    template : ` 
        <h1 md-dialog-title>Spiel ist fertig</h1>
            <div md-dialog-content>Zurück zu Index Page</div>
        <div md-dialog-actions>
          <button md-button (click)="redirect()">Ja</button>
        </div>
    `,
})
export class BackToIndexModal {
    public redirect() {
        window.location.href = 'https://' + window.location.host + '/index';
    }
}
