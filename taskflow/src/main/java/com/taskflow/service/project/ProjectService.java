package com.taskflow.service.project;

import com.taskflow.dto.project.ProjectRequest;
import com.taskflow.dto.project.ProjectResponse;
import com.taskflow.exception.auth.UserNotAuthenticatedException;
import com.taskflow.exception.project.ProjectNameExistsException;
import com.taskflow.model.organisation.Organisation;
import com.taskflow.model.project.MembershipStatus;
import com.taskflow.model.project.Project;
import com.taskflow.model.project.ProjectMember;
import com.taskflow.model.project.ProjectRole;
import com.taskflow.repository.organisation.OrganisationRepository;
import com.taskflow.repository.project.ProjectMemberRepository;
import com.taskflow.repository.project.ProjectRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final OrganisationRepository organisationRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            OrganisationRepository organisationRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.organisationRepository = organisationRepository;
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest projectRequest) {

        // 1. Identify the authenticated user.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String userId)) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        UUID creatorId = UUID.fromString(userId);
        UUID organisationId = projectRequest.getOrganisationId();

        // 2. Find the organisation.
        Organisation organisation = organisationRepository
                .findById(organisationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Organisation not found"));

        // 3. Only the organisation owner can create projects for now.
        if (!creatorId.equals(organisation.getOwnerId())) {
            throw new IllegalArgumentException(
                    "You do not have permission to create projects in this organisation"
            );
        }

        // 4. Normalize the project name.
        String name = projectRequest.getName().trim();

        // 5. Prevent duplicate project names within the same organisation.
        if (projectRepository.existsByNameIgnoreCaseAndOrganisation_Id(
                name, organisationId)) {
            throw new ProjectNameExistsException(
                    "A project with this name already exists in this organisation"
            );
        }

        // 6. Create the project.
        Project project = new Project();
        project.setName(name);
        project.setOrganisation(organisation);
        project.setCreatedBy(creatorId);

        Project savedProject = projectRepository.save(project);

        // 7. Automatically make the creator the first active project admin.
        ProjectMember creatorMembership = new ProjectMember();
        creatorMembership.setProject(savedProject);
        creatorMembership.setUserId(creatorId);
        creatorMembership.setRole(ProjectRole.PROJECT_ADMIN);
        creatorMembership.setStatus(MembershipStatus.ACTIVE);

        projectMemberRepository.save(creatorMembership);

        // 8. Return the project response.
        return toProjectResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects() {

        // 1. Identify the authenticated user.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String userId)) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        UUID currentUserId = UUID.fromString(userId);

        // 2. Find this user's active project memberships.
        List<ProjectMember> memberships =
                projectMemberRepository.findAllByUserIdAndStatus(
                        currentUserId,
                        MembershipStatus.ACTIVE
                );

        // 3. Return projects the user is an active member of.
        return memberships.stream()
                .map(ProjectMember::getProject)
                .distinct()
                .map(this::toProjectResponse)
                .toList();
    }

    private ProjectResponse toProjectResponse(Project project) {
        Organisation organisation = project.getOrganisation();

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                organisation.getId(),
                organisation.getName(),
                project.getCreatedBy(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByOrganisation(UUID organisationId) {

        // 1. Identify the authenticated user.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String userId)) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        UUID currentUserId = UUID.fromString(userId);

        // 2. Verify that the organisation exists.
        Organisation organisation = organisationRepository
                .findById(organisationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Organisation not found"));

        // 3. Find the user's active project memberships.
        List<ProjectMember> memberships =
                projectMemberRepository.findAllByUserIdAndStatus(
                        currentUserId,
                        MembershipStatus.ACTIVE
                );

        // 4. Return only projects belonging to the selected organisation.
        return memberships.stream()
                .map(ProjectMember::getProject)
                .filter(project ->
                        project.getOrganisation().getId()
                                .equals(organisation.getId()))
                .distinct()
                .map(this::toProjectResponse)
                .toList();
    }
}
