import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RecipeModel } from '../components/home/recipe.mode';
import { PageResponse } from '../components/home/page-response.model';
import { environment } from 'src/environments/environment';

const API_BASE_URL : string = environment.api_base_url;

const API_URL = API_BASE_URL + 'recipes/';

const httpOptions = {
    headers: new HttpHeaders({ 
      'Content-Type': 'application/json'
    })
  };

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  constructor(private http: HttpClient) { }

  getAllRecipies(page: number = 0, perpage: number = 10): Observable<PageResponse<RecipeModel>> {
    const requestUrl = `${API_URL}?page=${page}&size=${perpage}`;
    return this.http.get<PageResponse<RecipeModel>>(requestUrl, httpOptions);
  }

  getRecipe(guid: string): Observable<RecipeModel> {
    return this.http.get<RecipeModel>(`${API_URL}`+ guid, httpOptions);
  }

  createRecipe(requestBody: RecipeModel): Observable<RecipeModel> {
    return this.http.post<RecipeModel>(API_URL, requestBody, httpOptions);
  }

  updateRecipe(guid: string, requestBody: RecipeModel): Observable<RecipeModel> {
    return this.http.put<RecipeModel>(`${API_URL}`+ guid, requestBody, httpOptions);
  }

  deleteRecipe(uuid: string) : Observable<void> {
    return this.http.delete<void>(`${API_URL}`+ uuid, httpOptions);
  }
}