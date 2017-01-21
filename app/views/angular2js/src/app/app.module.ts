import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { AppComponent } from './pages/mainApp/app.component';
import { MaterialModule } from '@angular/material';
import { MyRoutingModule } from './routes/app-routing.module';


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    MyRoutingModule,
      MaterialModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
