import { Component, inject, input } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UserData } from '../../stores/users-store';

@Component({
  selector: 'app-users-list',
  imports: [RouterLink],
  templateUrl: './users-list.html',
  styleUrl: './users-list.css',
})
export class UsersList {

  users = input<UserData[]>([]); // Comes from path resolver

  private router = inject(Router);

  protected navigateBack() {
    this.router.navigate(['/']);
  }

}
