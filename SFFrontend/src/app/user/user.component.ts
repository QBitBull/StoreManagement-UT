import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from '../enum/notification-type.enum';
import { Role } from '../enum/role.enum';
import { CustomHttpResponse } from '../model/custom-http-response';
import { FileUploadStatus } from '../model/file-upload.status';
import { Finance } from '../model/finance';
import { Product } from '../model/product';
import { User } from '../model/user';
import { Utility } from '../model/utility';
import { AuthenticationService } from '../service/authentication.service';
import { FinanceService } from '../service/finance.service';
import { NotificationService } from '../service/notification.service';
import { PriceService } from '../service/price.service';
import { ProductService } from '../service/product.service';
import { UserService } from '../service/user.service';
import { UtilityService } from '../service/utility.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, OnDestroy {
  private titleSubject = new BehaviorSubject<string>('Users');
  public titleAction$ = this.titleSubject.asObservable();

  public products: Product[];
  public product: Product;
  
  public users: User[];
  public user: User;
  
  public utilities: Utility[];
  public utility: Utility;

  public finances: Finance[];
  public finance: Finance;

  public refreshing: boolean;
  
  public selectedUtility: Utility;

  public selectedFinance: Finance;
  
  public selectedUser: User;
  
  public selectedProduct: Product;
  
  public fileName: string;
  public profileImage: File;
  private subscriptions: Subscription[] = [];
  
  public editUser = new User();
  
  public editUtility = new Utility();

  public editFinance = new Finance();

  public editProduct = new Product();

  private currentDate: string;

  private currentNameUtility: string;
  
  private currentUsername: string;
  
  private currentProductName: string;
  
  public fileStatus = new FileUploadStatus();

  constructor(private router: Router, private authenticationService: AuthenticationService,
    private priceService : PriceService,
    private productService: ProductService,
    private utilityService: UtilityService,
    private financeService: FinanceService,          
    private userService: UserService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.user = this.authenticationService.getUserFromLocalCache();
    this.getUsers(true);
    this.getProducts(true);
    this.getUtilities(true);
    this.getFinances(true);
  }

  public changeTitle(title: string): void {
    this.titleSubject.next(title);
  }

  // users

  public getUsers(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          this.userService.addUsersToLocalCache(response);
          this.users = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} user(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );

  }

  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
    this.clickButton('openUserInfo');
  }

  public onProfileImageChange(fileName: string, profileImage: File): void {
    this.fileName =  fileName;
    this.profileImage = profileImage;
  }

  public saveNewUser(): void {
    this.clickButton('new-user-save');
  }

  public onAddNewUser(userForm: NgForm): void {
    const formData = this.userService.createUserFormData(null, userForm.value, this.profileImage);
    this.subscriptions.push(
      this.userService.addUser(formData).subscribe(
        (response: User) => {
          this.clickButton('new-user-close');
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          userForm.reset();
          this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} added successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
      );
  }

  public onUpdateUser(): void {
    const formData = this.userService.createUserFormData(this.currentUsername, this.editUser, this.profileImage);
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.clickButton('closeEditUserModalButton');
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
      );
  }

  public onUpdateCurrentUser(user: User): void {
    this.refreshing = true;
    this.currentUsername = this.authenticationService.getUserFromLocalCache().username;
    const formData = this.userService.createUserFormData(this.currentUsername, user, this.profileImage);
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.authenticationService.addUserToLocalCache(response);
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
          this.profileImage = null;
        }
      )
      );
  }

  public onUpdateProfileImage(): void {
    const formData = new FormData();
    formData.append('username', this.user.username);
    formData.append('profileImage', this.profileImage);
    this.subscriptions.push(
      this.userService.updateProfileImage(formData).subscribe(
        (event: HttpEvent<any>) => {
          this.reportUploadProgress(event);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.fileStatus.status = 'done';
        }
      )
    );
  }

  private reportUploadProgress(event: HttpEvent<any>): void {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        this.fileStatus.percentage = Math.round(100 * event.loaded / event.total);
        this.fileStatus.status = 'progress';
        break;
      case HttpEventType.Response:
        if (event.status === 200) {
          this.user.profileImageURL = `${event.body.profileImageUrl}?time=${new Date().getTime()}`;
          this.sendNotification(NotificationType.SUCCESS, `${event.body.firstName}\'s profile image updated successfully`);
          this.fileStatus.status = 'done';
          break;
        } else {
          this.sendNotification(NotificationType.ERROR, `Unable to upload image. Please try again`);
          break;
        }
      default:
        `Finished all processes`;
    }
  }

  public updateProfileImage(): void {
    this.clickButton('profile-image-input');
  }

  public onLogOut(): void {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS, `You've been successfully logged out`);
  }

  public onResetPassword(emailForm: NgForm): void {
    this.refreshing = true;
    const emailAddress = emailForm.value['reset-password-email'];
    this.subscriptions.push(
      this.userService.resetPassword(emailAddress).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refreshing = false;
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, error.error.message);
          this.refreshing = false;
        },
        () => emailForm.reset()
      )
    );
  }

  public onPriceCalculator(priceForm : NgForm): void {
    this.refreshing = true;
    const profit = priceForm.value['profit'];
    const sales = priceForm.value['sales'];
    this.subscriptions.push(
      this.priceService.calculate(profit, sales).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refreshing = false;
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, error.error.message);
          this.refreshing = false;
        },
        () => priceForm.reset()
      )
    );
  }

  public onUpdatePrice(priceForm : NgForm): void {
    this.refreshing = true;
    const profit = priceForm.value['upProfit'];
    this.subscriptions.push(
      this.priceService.updatePrices(profit).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refreshing = false;
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, error.error.message);
          this.refreshing = false;
        },
        () => priceForm.reset()
      )
    );
  }

  public onDeleteUder(username: string): void {
    this.subscriptions.push(
      this.userService.deleteUser(username).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getUsers(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  public onEditUser(editUser: User): void {
    this.editUser = editUser;
    this.currentUsername = editUser.username;
    this.clickButton('openUserEdit');
  }

  public searchUsers(searchTerm: string): void {
    const results: User[] = [];
    for (const user of this.userService.getUsersFromLocalCache()) {
      if (user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.userID.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) {
          results.push(user);
      }
    }
    this.users = results;
    if (results.length === 0 || !searchTerm) {
      this.users = this.userService.getUsersFromLocalCache();
    }
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN;
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  // products

  public searchProducts(searchTerm: string): void {
    const results: Product[] = [];
    for (const product of this.productService.getProductsFromLocalCache()) {
      if (product.productName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          product.nameBrand.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          product.nameCategory.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ) {
          results.push(product);
      }
    }
    this.products = results;
    if (results.length === 0 || !searchTerm) {
      this.products = this.productService.getProductsFromLocalCache();
    }
  }

  public getProducts(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.productService.getProducts().subscribe(
        (response: Product[]) => {
          this.productService.addProductsToLocalCache(response);
          this.products = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} product(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  
  }

  public onSelectProduct(selectedProduct: Product): void {
    this.selectedProduct = selectedProduct;
    this.clickButton('openProductInfo');
  }

  public onEditProduct(editProduct: Product): void {
    this.editProduct = editProduct;
    this.currentProductName = editProduct.productName;
    this.clickButton('openProductEdit');
  }

  public onDeleteProduct(productName: string): void {
    this.subscriptions.push(
      this.productService.deleteProduct(productName).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getProducts(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  public onAddNewProduct(productForm: NgForm): void {
    const formData = this.productService.createProductFormData(null, productForm.value);
    this.subscriptions.push(
      this.productService.addProduct(formData).subscribe(
        (response: Product) => {
          this.clickButton('new-product-close');
          this.getProducts(false);
          this.fileName = null;
          this.profileImage = null;
          productForm.reset();
          this.sendNotification(NotificationType.SUCCESS, `${response.productName} added successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
      );
  }

  public saveNewProduct(): void {
    this.clickButton('new-product-save');
  }

  public onUpdateProduct(): void {
    const formData = this.productService.createProductFormData(this.currentProductName, this.editProduct);
    this.subscriptions.push(
      this.productService.updateProduct(formData).subscribe(
        (response: Product) => {
          this.clickButton('closeEditProductModalButton');
          this.getProducts(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(NotificationType.SUCCESS, `${response.productName} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
      );
  }

  // utilities

  public searchUtilities(searchTerm: string): void {
    const results: Utility[] = [];
    for (const utility of this.utilityService.getUtilitiesFromLocalCache()) {
      if (utility.nameUtility.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) {
          results.push(utility);
      }
    }
    this.utilities = results;
    if (results.length === 0 || !searchTerm) {
      this.utilities = this.utilityService.getUtilitiesFromLocalCache();
    }
  }

  public getUtilities(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.utilityService.getUtilities().subscribe(
        (response: Utility[]) => {
          this.utilityService.addUtilitiesToLocalCache(response);
          this.utilities = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} utility/ie(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  
  }

  public onSelectUtility(selectedUtility: Utility): void {
    this.selectedUtility = selectedUtility;
    this.clickButton('openUtilityInfo');
  }

  public onEditUtility(editUtility: Utility): void {
    this.editUtility = editUtility;
    this.currentNameUtility = editUtility.nameUtility;
    this.clickButton('openUtilityEdit');
  }

  public onDeleteUtility(nameUtility: string): void {
    this.subscriptions.push(
      this.utilityService.deleteUtility(nameUtility).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getUtilities(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  public onAddNewUtility(utilityForm: NgForm): void {
    const formData = this.utilityService.createUtilityFormData(null, utilityForm.value);
    this.subscriptions.push(
      this.utilityService.addUtility(formData).subscribe(
        (response: Utility) => {
          this.clickButton('new-utility-close');
          this.getUtilities(false);
          this.fileName = null;
          this.profileImage = null;
          utilityForm.reset();
          this.sendNotification(NotificationType.SUCCESS, `${response.nameUtility} added successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
      );
  }

  public saveNewutility(): void {
    this.clickButton('new-utility-save');
  }

  public onUpdateUtility(): void {
    const formData = this.utilityService.createUtilityFormData(this.currentNameUtility, this.editUtility);
    this.subscriptions.push(
      this.utilityService.updateUtility(formData).subscribe(
        (response: Utility) => {
          this.clickButton('closeEditUtilityModalButton');
          this.getUtilities(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(NotificationType.SUCCESS, `${response.nameUtility} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
      );
  }

  // finances

  public searchFinances(searchTerm: string): void {
    const results: Finance[] = [];
    for (const finance of this.financeService.getFinancesFromLocalCache()) {
      if (finance.date.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) {
          results.push(finance);
      }
    }
    this.finances = results;
    if (results.length === 0 || !searchTerm) {
      this.finances = this.financeService.getFinancesFromLocalCache();
    }
  }

  public getFinances(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.financeService.getFinances().subscribe(
        (response: Finance[]) => {
          this.financeService.addFinancesToLocalCache(response);
          this.finances = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} finance(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  
  }

  public onSelectFinance(selectedFinance: Finance): void {
    this.selectedFinance = selectedFinance;
    this.clickButton('openFinanceInfo');
  }

  public onEditFinance(editFinance: Finance): void {
    this.editFinance = editFinance;
    this.currentDate = editFinance.date;
    this.clickButton('openFinanceEdit');
  }

  public onDeleteFinance(date: string): void {
    this.subscriptions.push(
      this.financeService.deleteFinance(date).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getFinances(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  public onAddNewFinance(financeForm: NgForm): void {
    const formData = this.financeService.createFinanceFormData(null, financeForm.value);
    this.subscriptions.push(
      this.financeService.addFinance(formData).subscribe(
        (response: Finance) => {
          this.clickButton('new-finance-close');
          this.getFinances(false);
          this.fileName = null;
          this.profileImage = null;
          financeForm.reset();
          this.sendNotification(NotificationType.SUCCESS, `Finances on ${response.date} added successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
      );
  }

  public saveNewFinance(): void {
    this.clickButton('new-finance-save');
  }

  public onUpdateFinance(): void {
    const formData = this.financeService.createFinanceFormData(this.currentDate, this.editFinance);
    this.subscriptions.push(
      this.financeService.updateFinance(formData).subscribe(
        (response: Finance) => {
          this.clickButton('closeEditFinanceModalButton');
          this.getFinances(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(NotificationType.SUCCESS, `Finances on ${response.date} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
      );
  }

}



