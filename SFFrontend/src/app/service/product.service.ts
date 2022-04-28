import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../model/custom-http-response';
import { Product } from '../model/product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public getProducts(): Observable<Product[]>{
    return this.http.get<Product[]>(`${this.host}/product/list`);
  }

  public findProduct(productName: string): Observable<Product>{
    return this.http.get<Product>(`${this.host}/product/find/${productName}`);
  }

  public addProduct(formData: FormData): Observable<Product>{
    return this.http.post<Product>(`${this.host}/product/add`, formData);
  }

  public updateProduct(formData: FormData): Observable<Product>{
    return this.http.post<Product>(`${this.host}/product/update`, formData);
  }

  public deleteProduct(productName: string): Observable<CustomHttpResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/product/delete/${productName}`);
  }

  public addProductsToLocalCache(products: Product[]): void{
    localStorage.setItem('products', JSON.stringify(products));
  }

  public getProductsFromLocalCache(): Product[] {
    if(localStorage.getItem('products')){
      return JSON.parse(localStorage.getItem('products'));
    }
    return null;
  }

  public createProductFormData(currentProductName: string, product: Product): FormData {
    const formData = new FormData();
    formData.append('currentProductName', currentProductName);
    formData.append('productName', product.productName);
    formData.append('boughtPrice',product.boughtPrice);
    formData.append('soldPrice',product.soldPrice);
    formData.append('nameBrand',product.nameBrand);
    formData.append('nameCategory',product.nameCategory);
    formData.append('quantity',product.quantity);
    return formData;
  } 
}