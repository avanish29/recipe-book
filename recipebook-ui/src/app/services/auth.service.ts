import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const AUTH_API = 'http://localhost:8080/';

const httpOptions = {
  headers: new HttpHeaders({ 
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'signin', {
      username,
      password
    }, httpOptions);
  }

  register(firstName: string, lastName: string, email: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'signup', JSON.stringify({
      'firstName': firstName, 'lastName': lastName, 'email': email, 'password': password
    }), httpOptions);
  }

  refreshToken(token: string) {
    return this.http.post(AUTH_API + 'refresh', {
      refreshToken: token
    }, httpOptions);
  }
}