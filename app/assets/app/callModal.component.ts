import {Component} from '@angular/core';
import {MdDialogRef} from '@angular/material';

@Component({
    selector: 'call-modal',
    template : ` 
        <h1 md-dialog-title>Anruf</h1>
            <div md-dialog-content>{{ user }} ruf dich an um ein Spiel zu starten</div>
        <div md-dialog-actions>
          <button md-button (click)="dialogRef.close('a')">Accept</button>
          <button md-button (click)="dialogRef.close('d')">Deny</button>
        </div>
    `,
})
export class CallModal {
    public user: string;
    constructor(public dialogRef: MdDialogRef<CallModal>) {}
}
