
import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { OrganisationService } from '../../core/services/organisation.service';
import { ProjectService } from '../../core/services/project.service';

import { OrganisationResponse } from '../organisation/organisation-model';
import { ProjectResponse } from '../project/models/project.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private readonly organisationService = inject(OrganisationService);
  private readonly projectService = inject(ProjectService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  organisations = signal<OrganisationResponse[]>([]);
  projects = signal<ProjectResponse[]>([]);

  selectedOrganisation = signal<OrganisationResponse | null>(null);

  isLoading = signal(true);
  isProjectsLoading = signal(false);
  isSubmitting = signal(false);
  isCreateModalOpen = signal(false);
  isProjectModalOpen = signal(false);
  isCreatingProject = signal(false);

  name = '';
  projectName = '';

  errorMessage = signal('');
  projectErrorMessage = signal('');
  projectFormErrorMessage = signal('');
  successMessage = signal('');

  ngOnInit(): void {
    this.loadOrganisations();
  }

  // Load organisations only when the dashboard opens.
  loadOrganisations(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.organisationService.getOrganisation().subscribe({
      next: (response) => {
        this.organisations.set(response);
        this.isLoading.set(false);

        const organisationId = this.route.snapshot.queryParamMap.get('organisationId');
        const organisation = response.find((item) => item.id === organisationId);

        if (organisation) {
          this.selectOrganisation(organisation);
        }
      },
      error: (error) => {
        console.error('Failed to load organisations:', error);

        this.errorMessage.set(
          error.error?.message || 'Failed to load organisations',
        );

        this.isLoading.set(false);
      },
    });
  }


selectOrganisation(organisation: OrganisationResponse): void {
  this.selectedOrganisation.set(organisation);
  this.projects.set([]);
  this.projectErrorMessage.set('');
  this.isProjectsLoading.set(true);

  this.projectService
    .getProjectsByOrganisation(organisation.id)
    .subscribe({
      next: (response) => {
        // Ignore the response if another organisation was selected.
        if (this.selectedOrganisation()?.id !== organisation.id) {
          return;
        }

        this.projects.set(response);
        this.isProjectsLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load organisation projects:', error);

        if (this.selectedOrganisation()?.id === organisation.id) {
          this.projectErrorMessage.set(
            error.error?.message || 'Failed to load projects',
          );

          this.isProjectsLoading.set(false);
        }
      },
    });
}

openProject(project: ProjectResponse): void {
  void this.router.navigate(['/projects', project.id, 'tasks']);
}


  backToOrganisations(): void {
    this.selectedOrganisation.set(null);
    this.projects.set([]);
    this.projectErrorMessage.set('');
    this.successMessage.set('');
  }

  openCreateProjectModal(): void {
    this.projectName = '';
    this.projectFormErrorMessage.set('');
    this.isProjectModalOpen.set(true);
  }

  closeCreateProjectModal(): void {
    if (this.isCreatingProject()) {
      return;
    }

    this.isProjectModalOpen.set(false);
  }

  createProject(): void {
    const organisation = this.selectedOrganisation();
    const projectName = this.projectName.trim();

    if (!organisation) {
      return;
    }

    if (projectName.length < 2) {
      this.projectFormErrorMessage.set(
        'Project name must contain at least 2 characters',
      );
      return;
    }

    this.isCreatingProject.set(true);
    this.projectFormErrorMessage.set('');
    this.successMessage.set('');

    this.projectService
      .createProject({ name: projectName, organisationId: organisation.id })
      .subscribe({
        next: (response) => {
          if (this.selectedOrganisation()?.id === organisation.id) {
            this.projects.update((projects) => [response, ...projects]);
          }

          this.isProjectModalOpen.set(false);
          this.projectName = '';
          this.successMessage.set(
            `Project "${response.name}" created successfully`,
          );
          this.isCreatingProject.set(false);
        },
        error: (error) => {
          console.error('Failed to create project:', error);
          this.projectFormErrorMessage.set(
            error.error?.message || 'Failed to create project',
          );
          this.isCreatingProject.set(false);
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
          this.organisations.update((organisations) => [
            response,
            ...organisations,
          ]);

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
