import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="min-h-screen bg-slate-950 text-white">
      <header class="border-b border-slate-800">
        <div class="mx-auto flex max-w-6xl items-center justify-between px-6 py-5">
          <div class="flex items-center gap-3">
            <div class="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-400 font-black text-slate-950">
              T
            </div>
            <span class="text-xl font-bold">TaskFlow</span>
          </div>

          <button
            type="button"
            (click)="logout()"
            class="rounded-lg border border-slate-700 px-4 py-2 text-sm font-medium transition hover:bg-slate-800"
          >
            Sign out
          </button>
        </div>
      </header>

      <main class="mx-auto max-w-6xl px-6 py-12">
        <p class="text-sm font-semibold uppercase tracking-widest text-emerald-400">
          Your workspace
        </p>

        <h1 class="mt-3 text-4xl font-bold tracking-tight">
          Welcome to TaskFlow
        </h1>

        <p class="mt-4 max-w-xl leading-7 text-slate-400">
          Your account is connected. Next, we'll build organisations,
          invite members and manage projects and tasks.
        </p>

        <div class="mt-10 grid gap-5 sm:grid-cols-3">
          <div class="rounded-xl border border-slate-800 bg-slate-900 p-6">
            <p class="text-sm text-slate-400">Organisations</p>
            <p class="mt-3 text-xl font-semibold">Coming next</p>
          </div>

          <div class="rounded-xl border border-slate-800 bg-slate-900 p-6">
            <p class="text-sm text-slate-400">Projects</p>
            <p class="mt-3 text-xl font-semibold">Coming soon</p>
          </div>

          <div class="rounded-xl border border-slate-800 bg-slate-900 p-6">
            <p class="text-sm text-slate-400">Tasks</p>
            <p class="mt-3 text-xl font-semibold">Coming soon</p>
          </div>
        </div>
      </main>
    </div>
  `,
})
export class Dashboard {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }
}