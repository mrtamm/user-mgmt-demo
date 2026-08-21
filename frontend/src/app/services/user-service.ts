import { catchError } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { UserData } from '../stores/users-store';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  constructor(private http: HttpClient) {
  }

  fetchAllUsers() {
    return this.http.get(`${environment.apiUrl}/v1/users`).pipe(catchError(_ => []));
  }

  addUser(data: UserData) {
    return this.http.post(`${environment.apiUrl}/v1/users`, data);
  }

  updateUser(data: UserData) {
    if (typeof data.id !== "string") throw new Error("Cannot update a user without ID.");
    return this.http.post(`${environment.apiUrl}/v1/users/${data.id}`, {
      "firstName": data.firstName,
      "lastName": data.lastName,
      "email": data.email,
    });
  }

}
