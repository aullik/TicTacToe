import './rxjs-extensions';

import {NgModule}         from '@angular/core';
import {BrowserModule}    from '@angular/platform-browser';
import {FormsModule}      from '@angular/forms';
import {HttpModule}       from '@angular/http';
import { MaterialModule } from '@angular/material';

import {AppRoutingModule} from './app-routing.module';

import {AppComponent}     from './app.component';
import {MyIndexComponent}       from './myIndex.component';
import {HomeComponent}      from './home.component';
import {TictactoeComponent}     from './tictactoe.component';
import {PageNotFoundComponent}     from './pageNotFound.component';
import {AuthComponent}      from './auth.component';
import {InfoComponent}     from './info.component';
import {PlayComponent}     from './play.component';
import {UsersComponent}     from './users.component';
import {ChatComponent}     from './chat.component';
import {SpinningModal}     from './spinningModal.component';
import {CallModal}     from './callModal.component';
/*
import {IntroComponent}   from './intro.component';
import {ManualComponent}  from './manual.component';
import {HistoryComponent} from './history.component';*/

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        AppRoutingModule,
        MaterialModule.forRoot(),
    ],
    declarations: [
        AppComponent,
        MyIndexComponent,
        HomeComponent,
        TictactoeComponent,
        PageNotFoundComponent,
        UsersComponent,
        PlayComponent,
        InfoComponent,
        AuthComponent,
        ChatComponent,
        SpinningModal,
        CallModal,
    ],
    bootstrap: [AppComponent, SpinningModal, CallModal],
})

export class AppModule {
}
