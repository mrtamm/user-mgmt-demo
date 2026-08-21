import {ApplicationConfig, isDevMode, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideHttpClient} from '@angular/common/http';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import {provideStore} from '@ngrx/store';

import {routes} from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(),
    provideRouter(routes, withComponentInputBinding()),
    provideStore(),
  ],
};
