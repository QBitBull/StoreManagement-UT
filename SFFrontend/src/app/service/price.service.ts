import { HttpClient, HttpErrorResponse, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../model/custom-http-response';

@Injectable({
  providedIn: 'root'
})
export class PriceService {

  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public calculate(profit: string, sales: string): Observable<CustomHttpResponse>{
    return this.http.get<CustomHttpResponse>(`${this.host}/priceCalculator/calculate/${profit}/${sales}`);
  }

  public updatePrices(profit: string): Observable<CustomHttpResponse>{
    return this.http.get<CustomHttpResponse>(`${this.host}/priceCalculator/update/${profit}`);
  }

  public createPriceFormData(profit: string, sales: string): FormData {
    const formData = new FormData();
    formData.append('profit', profit);
    formData.append('sales', sales);
    return formData;
  } 
}
