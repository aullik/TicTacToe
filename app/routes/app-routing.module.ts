import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// import { MaterialModule } from '@angular/material';
import { FormsModule } from '@angular/forms';
import { HttpModule, JsonpModule } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { MyIndexComponent }  from '../pages/index/index.component';
import { AuthComponent }  from '../pages/index/components/auth/auth.component';
import { InfoComponent }  from '../pages/index/components/info/info.component';
import { HomeComponent }  from '../pages/home/home.component';
import { TictactoeComponent }  from '../pages/tictactoe/tictactoe.component';
import { PlayComponent }  from '../pages/tictactoe/components/play.component';
import { ChatComponent }  from '../pages/shared-component/chat.component';
import { UsersComponent }  from '../pages/home/components/users.component';
import { PageNotFoundComponent }  from '../pages/pageNotFound/pageNotFound.component';

const routes: Routes = [
  { path: '', component: MyIndexComponent },
  { path: 'index',      component: HomeComponent },
  { path: 'tictactoe',      component: TictactoeComponent },
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
    imports: [
      RouterModule.forRoot(routes),
      // MaterialModule.forRoot(),
      FormsModule,
      HttpModule,
      JsonpModule,
      BrowserModule,
    ],
    exports: [RouterModule],
    declarations: [
      MyIndexComponent, HomeComponent, TictactoeComponent, PageNotFoundComponent,
      AuthComponent, InfoComponent, ChatComponent, UsersComponent, PlayComponent],
    providers: [],
})
export class MyRoutingModule { }
