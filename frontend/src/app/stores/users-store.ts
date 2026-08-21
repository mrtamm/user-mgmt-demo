import { inject } from '@angular/core';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { defer, map, Observable, of, tap } from 'rxjs';

import { UserService } from '../services/user-service';

export class UserData {
  firstName: string;
  lastName: string;
  email: string;
  id?: string;

  constructor(firstName: string, lastName: string, email: string, id?: string) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.id = id;
  }
}

export interface UsersState {
  users: UserData[];
}

const initialUsersState: UsersState = {
  users: [],
};

export const UsersStore = signalStore(
  { providedIn: 'root' },
  withState(initialUsersState),

  withMethods((store, userService = inject(UserService)) => ({

    fetchUsers(): Observable<UserData[]> {
      return userService.fetchAllUsers().pipe(
        tap((users: UserData[]) => patchState(store, { users: users })),
      );
    },

    getUser(userId: string) {
      return defer(() => {
        // 1. Check if user is already in the store array
        const existingUser = store.users().find((u) => u.id === userId);
        if (existingUser) {
          console.log('User found from store with id ', existingUser.id);
          return of(existingUser);
        }

        console.log('User was not found from the store of ' + store.users().length + ' users; fetching users...');
        // 2. Fetch all users if not found, update store, and retry lookup
        return this.fetchUsers().pipe(
          map((users: UserData[]) => users.find((u) => u.id === userId))
        );
      });
    },

    persist(request: UserData): Observable<UserData> {
      if (!request.id) {
        return userService.addUser(request).pipe(
          tap((result: UserData) => {
            patchState(store, ({ users: [...store.users(), result] }))
          }),
        )
      }

      return userService.updateUser(request).pipe(
        tap((result: UserData) => {
          patchState(store, ({ users: [...store.users().map(item => item.id === result.id ? result : item)] }))
        }),
      )
    },

  })),
);
