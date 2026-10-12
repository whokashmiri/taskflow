
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrganisationService } from '../../core/services/organisation.service';
import { OrganisationResponse } from '../organisation/models/organisation-model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private readonly organisationService = inject(OrganisationService);

  organisations = signal<OrganisationResponse[]>([]);
  isLoading = signal(true);
  isSubmitting = signal(false);
  isCreateModalOpen = signal(false);

  name = '';
  errorMessage = signal('');
  successMessage = signal('');

  ngOnInit(): void {
    this.loadOrganisations();
  }

  loadOrganisations(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.organisationService.getOrganisation().subscribe({
      next: (response) => {
        console.log('Organisations received:', response);

        this.organisations.set(response);
        this.isLoading.set(false);

        console.log('isLoading after success:', this.isLoading());
      },
      error: (error) => {
        console.error('Failed to load organisations:', error);

        this.errorMessage.set(
          error.error?.message || 'Failed to load organisations',
        );

        this.isLoading.set(false);

        console.log('isLoading after error:', this.isLoading());
      },
    });
  }


  openCreateModal(): void {
    this.name = '';
    this.errorMessage.set('');
    this.isCreateModalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.isSubmitting()) {
      return;
    }

    this.isCreateModalOpen.set(false);
  }

  createOrganisation(): void {
    const organisationName = this.name.trim();

    if (organisationName.length < 2) {
      this.errorMessage.set(
        'Organisation name must contain at least 2 characters',
      );
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set('');

    this.organisationService
      .createOrganisation({ name: organisationName })
      .subscribe({
        next: (response) => {
          this.organisations.update((organisations) => [response, ...organisations]);
          this.isCreateModalOpen.set(false);
          this.name = '';
          this.successMessage.set(
            `Organisation "${response.name}" created successfully`,
          );
          this.isSubmitting.set(false);
        },
        error: (error) => {
          console.error('Failed to create organisation:', error);
          this.errorMessage.set(
            error.error?.message || 'Failed to create organisation',
          );
          this.isSubmitting.set(false);
        },
      });
  }
}
