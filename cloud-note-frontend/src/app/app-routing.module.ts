import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PdfUploadComponent } from './pdf-upload/pdf-upload.component';
import { LibraryComponent } from './library/library.component';
import { SignupComponent } from './auth/signup/signup.component';
import { LoginComponent } from './auth/login/login.component';

const routes: Routes = [
  { path: 'upload', component: PdfUploadComponent },
  { path: 'library', component: LibraryComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }


//imports: [RouterModule.forRoot(routes)]: Configures the router at the root level with the route definitions provided in routes
//exports: [RouterModule]: Exports the RouterModule so that the routing configuration can be used in other parts of the application.