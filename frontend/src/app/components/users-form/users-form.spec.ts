import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersForm } from './users-form';

describe('UsersForm', () => {
  let component: UsersForm;
  let fixture: ComponentFixture<UsersForm>;

  beforeEach(async() => {
    await TestBed.configureTestingModule({
      imports: [UsersForm],
    }).compileComponents();

    fixture = TestBed.createComponent(UsersForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be invalid when required fields are empty', () => {
    expect(component.form().valid).toBeFalsy();

    expect(component.form().controls.firstName.hasError('required')).toBeTruthy();
    expect(component.form().controls.lastName.hasError('required')).toBeTruthy();
    expect(component.form().controls.email.hasError('required')).toBeTruthy();
  });

  it('should reject an invalid email address', () => {
    component.form().controls.firstName.setValue('Johnny');
    component.form().controls.lastName.setValue('Smith');
    component.form().controls.email.setValue('not-an-email');

    expect(component.form().valid).toBeFalsy();

    expect(component.form().controls.firstName.hasError('required')).toBeFalsy();
    expect(component.form().controls.lastName.hasError('required')).toBeFalsy();
    expect(component.form().controls.email.hasError('pattern')).toBeTruthy();
  });

  it('should be valid with correct values', () => {
    component.form().setValue({
      firstName: 'Johnny',
      lastName: 'Smith',
      email: 'johnny@example.com',
    });

    expect(component.form().valid).toBeTruthy();
  });
});
