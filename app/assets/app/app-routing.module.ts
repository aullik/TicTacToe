import {NgModule}             from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {MyIndexComponent}       from './myIndex.component';
import {HomeComponent}      from './home.component';
import {TictactoeComponent}     from './tictactoe.component';
import {PageNotFoundComponent}     from './pageNotFound.component';

const routes: Routes = [
    {path: '', component: MyIndexComponent},
    {path: 'index', component: HomeComponent},
    {path: 'tictactoe', component: TictactoeComponent},
    {path: '**', component: PageNotFoundComponent},
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})

export class AppRoutingModule {
}
