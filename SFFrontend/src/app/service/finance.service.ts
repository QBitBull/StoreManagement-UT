import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../model/custom-http-response';
import { Finance } from '../model/finance';

@Injectable({providedIn: 'root'})
export class FinanceService {

  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public getFinances(): Observable<Finance[]>{
    return this.http.get<Finance[]>(`${this.host}/finance/list`);
  }

  public findFinance(date: string): Observable<Finance>{
    return this.http.get<Finance>(`${this.host}/finance/find/${date}`);
  }

  public addFinance(formData: FormData): Observable<Finance>{
    return this.http.post<Finance>(`${this.host}/finance/add`, formData);
  }

  public updateFinance(formData: FormData): Observable<Finance>{
    return this.http.post<Finance>(`${this.host}/finance/update`, formData);
  }

  public deleteFinance(dateFinance: string): Observable<CustomHttpResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/finance/delete/${dateFinance}`);
  }

  public createFinanceFormData(currentDate: string, finance: Finance): FormData {
    const formData = new FormData();
    formData.append('currentDate', currentDate);
    formData.append('date', finance.date);
    formData.append('income',finance.income);
    return formData;
  } 

  public addFinancesToLocalCache(finances: Finance[]): void{
    localStorage.setItem('finances', JSON.stringify(finances));
  }

  public getFinancesFromLocalCache(): Finance[] {
    if(localStorage.getItem('finances')){
      return JSON.parse(localStorage.getItem('finances'));
    }
    return null;
  }

}
