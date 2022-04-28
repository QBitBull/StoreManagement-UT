import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../model/custom-http-response';
import { Utility } from '../model/utility';

@Injectable({
  providedIn: 'root'
})
export class UtilityService {

  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public getUtilities(): Observable<Utility[]>{
    return this.http.get<Utility[]>(`${this.host}/utility/list`);
  }

  public findUtility(nameUtility: string): Observable<Utility>{
    return this.http.get<Utility>(`${this.host}/utility/find/${nameUtility}`);
  }

  public addUtility(formData: FormData): Observable<Utility>{
    return this.http.post<Utility>(`${this.host}/utility/add`, formData);
  }

  public updateUtility(formData: FormData): Observable<Utility>{
    return this.http.post<Utility>(`${this.host}/utility/update`, formData);
  }

  public deleteUtility(nameUtility: string): Observable<CustomHttpResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/utility/delete/${nameUtility}`);
  }

  public createUtilityFormData(currentNameUtility: string, utility: Utility): FormData {
    const formData = new FormData();
    formData.append('currentNameUtility', currentNameUtility);
    formData.append('nameUtility', utility.nameUtility);
    formData.append('priceUtility',utility.priceUtility);
    return formData;
  } 
  public addUtilitiesToLocalCache(utilities: Utility[]): void{
    localStorage.setItem('utilities', JSON.stringify(utilities));
  }

  public getUtilitiesFromLocalCache(): Utility[] {
    if(localStorage.getItem('utilities')){
      return JSON.parse(localStorage.getItem('utilities'));
    }
    return null;
  }
}
