import { Component , inject } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { HttpErrorResponse } from '@angular/common/http';

import { OrganisationService } from "../../../../core/services/organisation.service";

@Component({
    selector: 'app-create-organisation',
    standalone: true,
    imports: [FormsModule],
    templateUrl: './create-organisation.html'
})

export class CreateOrganisation {
    private organisationService = inject(OrganisationService);

    name = '';
    isSubmitting = false;
    successMessage = '';
    errorMessage = '';

    onSubmit(): void {
        const organisationName = this.name.trim();

        if(!organisationName){
            this.errorMessage = 'Organisation name required';
            this.successMessage = '';
            return;
        }

        this.isSubmitting =  true ;
        this.successMessage = '';
        this.errorMessage = '';

        this.organisationService
        .createOrganisation({name: organisationName})
        
.subscribe({
  next: (response) => {
    console.log('Organisation created:', response);

    this.successMessage =
      `Organisation "${response.name}" created successfully`;

    this.name = '';
    this.isSubmitting = false;

    console.log('isSubmitting:', this.isSubmitting);
  },
  error: (error) => {
    console.error('Create organisation error:', error);

    this.errorMessage =
      error.error?.message ||
      'Failed to create organisation';

    this.isSubmitting = false;
  },
  complete: () => {
    console.log('Create organisation request completed');
  }
});

    }
}