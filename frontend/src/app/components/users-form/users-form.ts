import { Component, computed, inject, input, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserData, UsersStore } from '../../stores/users-store';
import { Router } from '@angular/router';

/**
 * UsersForm covers both create-new and update-existing user cases.
 * When the input UserData has an ID, an existing record will be updated instead of creating a new.
 */
@Component({
  selector: 'app-users-form',
  imports: [ReactiveFormsModule],
  templateUrl: './users-form.html',
  styleUrl: './users-form.css',
})
export class UsersForm {

  private router = inject(Router);

  private store = inject(UsersStore);

  protected data = input<UserData>(new UserData('', '', '')); // Comes from path resolver

  form = computed(() => new FormGroup({
    firstName: new FormControl(this.data().firstName, {
      nonNullable: true,
      validators: [Validators.required],
    }),
    lastName: new FormControl(this.data().lastName, {
      nonNullable: true,
      validators: [Validators.required],
    }),
    email: new FormControl(this.data().email, {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/.*@[a-z0-9.-]+\.[a-z]{2,15}$/)],
    }),
  }));

  protected isNew = computed(() => !this.data() || !this.data().id);

  protected customErrors = signal<Object>({});

  protected cancel() {
    if (this.isNew()) {
      this.router.navigate(['/']);
    } else {
      this.router.navigate(['/users']);
    }
  }

  protected save() {
    const formData = this.form().getRawValue();
    const newData = new UserData(
      formData.firstName,
      formData.lastName,
      formData.email,
      this.data().id,
    );

    this.store.persist(newData).subscribe({
      next: () => this.cancel(),
      error: err => this.processError(err),
    });
  }

  protected get firstName() {
    return this.form().controls.firstName;
  }

  protected get lastName() {
    return this.form().controls.lastName;
  }

  protected get email() {
    return this.form().controls.email;
  }

  /**
   * Converts error-response to string. Attempts to extract known Problem-Details data to compose a
   * user-friendly message. Falls back to `response.message` or just response as string.
   */
  private processError(response) {
    console.log("HTTP request failed", response);
    const problem = response.error;

    if (problem && typeof problem.errors === "object") {
      this.customErrors.set(problem.errors)
      return;
    }

    let result = problem.title || response.message || (response + "");
    if (problem && typeof problem.detail === "string") {
      result += ":\n" + problem.detail;
    }
    window.alert(result);
  }
}
