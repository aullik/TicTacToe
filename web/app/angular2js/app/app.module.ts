import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
//import {MdCardModule} from '@angular2-material/card';
import {MdButtonModule} from '@angular2-material/button';
import {MdIconModule} from '@angular2-material/icon';
import {MdIconRegistry} from '@angular2-material/icon';
import { AppComponent }  from './pages/main/app.component';

import { MyRoutingModule } from './routes/app-routing.module';
//import { MyIndexComponent }  from './pages/index/index.component';
//import { SignupComponent }  from './pages/index/components/signup/signup.component';
//import { LoginComponent }  from './pages/index/components/login/login.component';
//import { HomeComponent }  from './pages/home/home.component';
//import { TictactoeComponent }  from './pages/tictactoe/tictactoe.component';
//import { PageNotFoundComponent }  from './pages/pageNotFound/pageNotFound.component';

/*
const appRoutes: Routes = [
  { path: '', component: MyIndexComponent },
  { path: 'home',      component: HomeComponent },
  { path: 'tictactoe',      component: TictactoeComponent },
  { path: '**', component: PageNotFoundComponent }
];
*/

@NgModule({
imports:      [ BrowserModule, MyRoutingModule ],
  declarations: [ AppComponent, MdButtonModule, MdIconModule],
  bootstrap:    [ AppComponent ],
  providers: [ MdIconRegistry ]
})
export class AppModule { }
