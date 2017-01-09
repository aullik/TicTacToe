import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MyIndexComponent }  from '../pages/index/index.component';
import { SignupComponent }  from '../pages/index/components/signup/signup.component';
import { LoginComponent }  from '../pages/index/components/login/login.component';
import { HomeComponent }  from '../pages/home/home.component';
import { TictactoeComponent }  from '../pages/tictactoe/tictactoe.component';
import { PageNotFoundComponent }  from '../pages/pageNotFound/pageNotFound.component';

const routes: Routes = [
  { path: '', component: MyIndexComponent },
  { path: 'home',      component: HomeComponent },
  { path: 'tictactoe',      component: TictactoeComponent },
  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
    declarations: [MyIndexComponent, HomeComponent, TictactoeComponent, PageNotFoundComponent,
    SignupComponent, LoginComponent],
    providers: [],
})
export class MyRoutingModule { }
