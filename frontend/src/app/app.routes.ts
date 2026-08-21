import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RedirectCommand,
  ResolveFn,
  Router,
  Routes,
} from '@angular/router';

import { Users } from './components/users/users';
import { UsersForm } from './components/users-form/users-form';
import { UsersList } from './components/users-list/users-list';
import { UserData, UsersStore } from './stores/users-store';
import { PageNotFound } from './components/page-not-found/page-not-found';
import { map, Observable, of } from 'rxjs';

/**
 * Provides UserData object for UsersForm.
 */
export const userResolver: ResolveFn<Observable<UserData | RedirectCommand>> = (route: ActivatedRouteSnapshot) => {
  const userId = route.paramMap.get('id')!;
  if (!userId) return of(new UserData('', '', ''));

  const store = inject(UsersStore);
  return store.getUser(userId).pipe(
    map((user?: UserData) => {
      if (user) return user;
      const router = inject(Router);
      const notFoundPath = router.parseUrl("/404");
      return new RedirectCommand(notFoundPath, { skipLocationChange: true });
    })
  );
};

/**
 * Provides UserData[] for UsersList.
 */
export const usersResolver: ResolveFn<Observable<UserData[]>> = () => {
  const store = inject(UsersStore);
  return store.fetchUsers();
};

export const routes: Routes = [
  { path: '', component: Users },
  { path: 'users', component: UsersList, resolve: { users: usersResolver } },
  { path: 'users/create', component: UsersForm, resolve: { data: userResolver } },
  { path: 'users/:id', component: UsersForm, resolve: { data: userResolver } },
  { path: '**', component: PageNotFound },
];
