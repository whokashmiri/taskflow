
package com.taskflow.service.organisation;

import com.taskflow.model.organisation.OrganisationMember;
import com.taskflow.model.organisation.OrganisationMembershipStatus;
import com.taskflow.model.organisation.OrganisationRole;
import com.taskflow.repository.organisation.OrganisationMemberRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganisationMemberService {

    private final OrganisationMemberRepository organisationMemberRepository;

    public OrganisationMemberService(
            OrganisationMemberRepository organisationMemberRepository
    ) {
        this.organisationMemberRepository = organisationMemberRepository;
    }

    // Save a new membership or update an existing one.
    @Transactional
    public OrganisationMember saveMembership(
            OrganisationMember membership
    ) {
        return organisationMemberRepository.save(membership);
    }

    // Find a user's membership, including revoked memberships.
    @Transactional(readOnly = true)
    public Optional<OrganisationMember> getMembership(
            UUID organisationId,
            UUID userId
    ) {
        return organisationMemberRepository
                .findByOrganisation_IdAndUserId(
                        organisationId,
                        userId
                );
    }

    // Check whether any membership record already exists.
    @Transactional(readOnly = true)
    public boolean membershipExists(
            UUID organisationId,
            UUID userId
    ) {
        return organisationMemberRepository
                .existsByOrganisation_IdAndUserId(
                        organisationId,
                        userId
                );
    }

    // Check whether a user is an active organisation member.
    @Transactional(readOnly = true)
    public boolean isActiveMember(
            UUID organisationId,
            UUID userId
    ) {
        return organisationMemberRepository
                .existsByOrganisation_IdAndUserIdAndStatus(
                        organisationId,
                        userId,
                        OrganisationMembershipStatus.ACTIVE
                );
    }

    // Find a user's membership with a specific role and status.
    @Transactional(readOnly = true)
    public Optional<OrganisationMember> getMembershipByRole(
            UUID organisationId,
            UUID userId,
            OrganisationMembershipStatus status,
            OrganisationRole role
    ) {
        return organisationMemberRepository
                .findByOrganisation_IdAndUserIdAndStatusAndRole(
                        organisationId,
                        userId,
                        status,
                        role
                );
    }

    // Get all active organisation memberships for a user.
    @Transactional(readOnly = true)
    public List<OrganisationMember> getActiveMembershipsByUser(
            UUID userId
    ) {
        return organisationMemberRepository
                .findAllByUserIdAndStatus(
                        userId,
                        OrganisationMembershipStatus.ACTIVE
                );
    }

    // Get all active members of an organisation.
    @Transactional(readOnly = true)
    public List<OrganisationMember> getActiveMembers(
            UUID organisationId
    ) {
        return organisationMemberRepository
                .findAllByOrganisation_IdAndStatus(
                        organisationId,
                        OrganisationMembershipStatus.ACTIVE
                );
    }

    // Check whether an active member has a particular role.
    @Transactional(readOnly = true)
    public boolean activeRoleExists(
            UUID organisationId,
            OrganisationRole role
    ) {
        return organisationMemberRepository
                .existsByOrganisation_IdAndRoleAndStatus(
                        organisationId,
                        role,
                        OrganisationMembershipStatus.ACTIVE
                );
    }

    // Delete a membership record permanently.
    @Transactional
    public void deleteMembership(UUID membershipId) {
        organisationMemberRepository.deleteById(membershipId);
    }
}
