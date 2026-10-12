 package com.taskflow.service.organisation;

import com.taskflow.dto.organisation.OrganisationRequest;
import com.taskflow.dto.organisation.OrganisationResponse;
import com.taskflow.exception.auth.UserNotAuthenticatedException;
import com.taskflow.exception.organisation.OrganisationAlreadyException;
import com.taskflow.exception.organisation.OrganisationEmptyException;
import com.taskflow.model.organisation.Organisation;
import com.taskflow.model.organisation.OrganisationMember;
import com.taskflow.model.organisation.OrganisationMembershipStatus;
import com.taskflow.model.organisation.OrganisationRole;
import com.taskflow.repository.organisation.OrganisationMemberRepository;
import com.taskflow.repository.organisation.OrganisationRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final OrganisationMemberRepository organisationMemberRepository;

    public OrganisationService(
            OrganisationRepository organisationRepository,
            OrganisationMemberRepository organisationMemberRepository
    ) {
        this.organisationRepository = organisationRepository;
        this.organisationMemberRepository = organisationMemberRepository;
    }

    @Transactional
    public OrganisationResponse createOrganisation(
            OrganisationRequest organisationRequest
    ) {
        // 1. Identify the authenticated user.
        UUID ownerId = getCurrentUserId();

        // 2. Validate and normalize the organisation name.
        String name = organisationRequest.getName().trim();

        if (name.isEmpty()) {
            throw new OrganisationEmptyException(
                    "Organisation name cannot be empty"
            );
        }

        // 3. Prevent duplicate organisation names.
        if (organisationRepository.existsByNameIgnoreCase(name)) {
            throw new OrganisationAlreadyException(
                    "An organisation with this name already exists"
            );
        }

        // 4. Create the organisation.
        Organisation organisation = new Organisation();
        organisation.setName(name);
        organisation.setOwnerId(ownerId);

        Organisation savedOrganisation =
                organisationRepository.save(organisation);

        // 5. Create the owner's active organisation membership.
        OrganisationMember ownerMembership = new OrganisationMember();
        ownerMembership.setOrganisation(savedOrganisation);
        ownerMembership.setUserId(ownerId);
        ownerMembership.setRole(OrganisationRole.ORG_OWNER);
        ownerMembership.setStatus(
                OrganisationMembershipStatus.ACTIVE
        );

        organisationMemberRepository.save(ownerMembership);

        // 6. Return the response.
        return toOrganisationResponse(savedOrganisation);
    }

    @Transactional(readOnly = true)
    public List<OrganisationResponse> getOrganisation() {

        // 1. Identify the authenticated user.
        UUID currentUserId = getCurrentUserId();

        // 2. Find all of this user's active organisation memberships.
        List<OrganisationMember> memberships =
                organisationMemberRepository.findAllByUserIdAndStatus(
                        currentUserId,
                        OrganisationMembershipStatus.ACTIVE
                );

        // 3. Return the organisations associated with those memberships.
        return memberships.stream()
                .map(OrganisationMember::getOrganisation)
                .distinct()
                .map(this::toOrganisationResponse)
                .toList();
    }

    private UUID getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String userId)) {
            throw new UserNotAuthenticatedException(
                    "User not authenticated"
            );
        }

        return UUID.fromString(userId);
    }

    private OrganisationResponse toOrganisationResponse(
            Organisation organisation
    ) {
        OrganisationResponse response = new OrganisationResponse();

        response.setId(organisation.getId());
        response.setName(organisation.getName());
        response.setOwnerId(
                String.valueOf(organisation.getOwnerId())
        );
        response.setCreatedAt(organisation.getCreatedAt());

        return response;
    }
}