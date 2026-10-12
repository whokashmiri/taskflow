package com.taskflow.repository.project;

import com.taskflow.model.project.MembershipStatus;
import com.taskflow.model.project.ProjectMember;
import com.taskflow.model.project.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository
        extends JpaRepository<ProjectMember, UUID> {

    // Find a user's membership in a specific project.
    Optional<ProjectMember> findByProject_IdAndUserId(
            UUID projectId,
            UUID userId
    );

    // Check whether a user already has a membership record.
    boolean existsByProject_IdAndUserId(
            UUID projectId,
            UUID userId
    );

    // Check whether a user is an active member of a project.
    boolean existsByProject_IdAndUserIdAndStatus(
            UUID projectId,
            UUID userId,
            MembershipStatus status
    );

    // Find an active membership with a specific role.
    Optional<ProjectMember> findByProject_IdAndUserIdAndStatusAndRole(
            UUID projectId,
            UUID userId,
            MembershipStatus status,
            ProjectRole role
    );

    // Find all active memberships for a user.
    List<ProjectMember> findAllByUserIdAndStatus(
            UUID userId,
            MembershipStatus status
    );

    // Find all active members of a project.
    List<ProjectMember> findAllByProject_IdAndStatus(
            UUID projectId,
            MembershipStatus status
    );

    // Check whether a project has an active member with a specific role.
    boolean existsByProject_IdAndRoleAndStatus(
            UUID projectId,
            ProjectRole role,
            MembershipStatus status
    );
}
