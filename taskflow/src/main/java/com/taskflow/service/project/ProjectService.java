
package com.taskflow.service.project;

import com.taskflow.dto.project.ProjectRequest;
import com.taskflow.dto.project.ProjectResponse;
import com.taskflow.exception.auth.UserNotAuthenticatedException;
import com.taskflow.exception.project.ProjectNameExistsException;
import com.taskflow.model.organisation.Organisation;
import com.taskflow.model.project.Project;
import com.taskflow.repository.organisation.OrganisationRepository;
import com.taskflow.repository.project.ProjectRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganisationRepository organisationRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            OrganisationRepository organisationRepository
    ) {
        this.projectRepository = projectRepository;
        this.organisationRepository = organisationRepository;
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest projectRequest) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof String userId)) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        UUID creatorId = UUID.fromString(userId);
        UUID organisationId = projectRequest.getOrganisationId();

        Organisation organisation = organisationRepository
                .findById(organisationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Organisation not found"));

        if (!creatorId.equals(organisation.getOwnerId())) {
            throw new IllegalArgumentException(
                    "You do not have permission to create projects in this organisation"
            );
        }

        String name = projectRequest.getName().trim();

        if (projectRepository.existsByNameIgnoreCaseAndOrganisation_Id(
                name, organisationId)) {
            throw new ProjectNameExistsException(
                    "A project with this name already exists in this organisation"
            );
        }

        Project project = new Project();
        project.setName(name);
        project.setOrganisation(organisation);
        project.setCreatedBy(creatorId);

        Project savedProject = projectRepository.save(project);

        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getName(),
                organisation.getId(),
                organisation.getName(),
                savedProject.getCreatedBy(),
                savedProject.getCreatedAt(),
                savedProject.getUpdatedAt()
        );
    }
}
