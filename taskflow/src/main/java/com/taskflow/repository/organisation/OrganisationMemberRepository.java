 package com.taskflow.repository.organisation;

import com.taskflow.model.organisation.OrganisationMember;
import com.taskflow.model.organisation.OrganisationMembershipStatus;
import com.taskflow.model.organisation.OrganisationRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganisationMemberRepository
        extends JpaRepository<OrganisationMember, UUID> {

    // Find a user's membership, including a revoked membership.
    Optional<OrganisationMember> findByOrganisation_IdAndUserId(
            UUID organisationId,
            UUID userId
    );

    // Check whether any membership record already exists.
    boolean existsByOrganisation_IdAndUserId(
            UUID organisationId,
            UUID userId
    );

    // Check whether the user is an active organisation member.
    boolean existsByOrganisation_IdAndUserIdAndStatus(
            UUID organisationId,
            UUID userId,
            OrganisationMembershipStatus status
    );

    // Find an active membership with a specific role.
    Optional<OrganisationMember>
    findByOrganisation_IdAndUserIdAndStatusAndRole(
            UUID organisationId,
            UUID userId,
            OrganisationMembershipStatus status,
            OrganisationRole role
    );

    // Find all active organisation memberships for a user.
    List<OrganisationMember> findAllByUserIdAndStatus(
            UUID userId,
            OrganisationMembershipStatus status
    );

    // Find all active members of an organisation.
    List<OrganisationMember> findAllByOrganisation_IdAndStatus(
            UUID organisationId,
            OrganisationMembershipStatus status
    );

    // Check whether an active member has a particular role.
    boolean existsByOrganisation_IdAndRoleAndStatus(
            UUID organisationId,
            OrganisationRole role,
            OrganisationMembershipStatus status
    );
}
