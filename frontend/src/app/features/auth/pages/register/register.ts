import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
})
export class Register {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  loading = false;
  errorMessage = '';

  registerForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  onSubmit(): void {
    this.errorMessage = '';

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading = true;

    this.authService.register(this.registerForm.getRawValue()).subscribe({
      next: () => {
        this.loading = false;

        void this.router.navigate(['/login'], {
          queryParams: { registered: 'true' },
        });
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;

        if (error.status === 0) {
          this.errorMessage =
            'Cannot connect to the server. Check that TaskFlow backend is running.';
          return;
        }

        const body = error.error;

        if (typeof body === 'string' && body.trim()) {
          this.errorMessage = body;
        } else if (
          body &&
          typeof body === 'object' &&
          'message' in body &&
          typeof body.message === 'string'
        ) {
          this.errorMessage = body.message;
        } else if (error.status === 409) {
          this.errorMessage = 'An account with these details already exists.';
        } else {
          this.errorMessage = 'Registration failed. Please try again.';
        }
      },
    });
  }
}