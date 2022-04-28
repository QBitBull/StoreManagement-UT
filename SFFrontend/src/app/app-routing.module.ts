import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FinanceComponent } from './finance/finance.component';
import { LoginComponent } from './login/login.component';
import { PriceComponent } from './price/price.component';
import { ProductComponent } from './product/product.component';
import { RegisterComponent } from './register/register.component';
import { UserComponent } from './user/user.component';
import { UtilityComponent } from './utility/utility.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'user/management', component: UserComponent},
  {path: 'product/management', component: ProductComponent},
  {path: 'finance/management', component: FinanceComponent},
  {path: 'utility/management', component: UtilityComponent},
  {path: 'price/management', component: PriceComponent},
  {path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
