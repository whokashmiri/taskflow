 package com.taskflow.controller.organisation;

import com.taskflow.exception.auth.UserNotAuthenticatedException;
import com.taskflow.model.organisation.OrganisationMember;
import com.taskflow.model.organisation.OrganisationMembershipStatus;
import com.taskflow.model.organisation.OrganisationRole;
import com.taskflow.service.organisation.OrganisationMemberService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationMemberController {

    private final OrganisationMemberService organisationMemberService;

    public OrganisationMemberController(
            OrganisationMemberService organisationMemberService
    ) {
        this.organisationMemberService = organisationMemberService;
    }

    // Get all active members of an organisation.
    // Only active organisation owners and admins can access this endpoint.
    @GetMapping("/{organisationId}/members")
    public List<OrganisationMemberResponse> getActiveMembers(
            @PathVariable UUID organisationId
    ) {
        UUID currentUserId = getCurrentUserId();

        requireOrganisationAdmin(organisationId, currentUserId);

        return organisationMemberService.getActiveMembers(organisationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Get the current user's membership in an organisation.
    @GetMapping("/{organisationId}/members/me")
    public OrganisationMemberResponse getMyMembership(
            @PathVariable UUID organisationId
    ) {
        UUID currentUserId = getCurrentUserId();

        OrganisationMember membership = organisationMemberService
                .getMembership(organisationId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organisation membership not found"
                ));

        return toResponse(membership);
    }

    // Get a specific member.
    // Users can view their own membership; owners/admins can view any member.
    @GetMapping("/{organisationId}/members/{userId}")
    public OrganisationMemberResponse getMembership(
            @PathVariable UUID organisationId,
            @PathVariable UUID userId
    ) {
        UUID currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            requireOrganisationAdmin(organisationId, currentUserId);
        }

        OrganisationMember membership = organisationMemberService
                .getMembership(organisationId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organisation membership not found"
                ));

        return toResponse(membership);
    }

    // Get all active organisation memberships belonging to the current user.
    @GetMapping("/me/memberships")
    public List<OrganisationMemberResponse> getMyActiveMemberships() {
        UUID currentUserId = getCurrentUserId();

        return organisationMemberService
                .getActiveMembershipsByUser(currentUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Extract the authenticated user's UUID from Spring Security.
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

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            throw new UserNotAuthenticatedException(
                    "Invalid authenticated user ID"
            );
        }
    }

    // Verify the caller is an active organisation owner or admin.
    private void requireOrganisationAdmin(
            UUID organisationId,
            UUID userId
    ) {
        OrganisationMember membership = organisationMemberService
                .getMembership(organisationId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorised to access this organisation"
                ));

        boolean activeMember =
                membership.getStatus() == OrganisationMembershipStatus.ACTIVE;

        boolean hasAdminRole =
                membership.getRole() == OrganisationRole.ORG_OWNER
                        || membership.getRole() == OrganisationRole.ORG_ADMIN;

        if (!activeMember || !hasAdminRole) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only active organisation owners and admins can perform this action"
            );
        }
    }

    // Return a DTO instead of exposing the JPA entity directly.
    private OrganisationMemberResponse toResponse(
            OrganisationMember membership
    ) {
        return new OrganisationMemberResponse(
                membership.getId(),
                membership.getOrganisation().getId(),
                membership.getUserId(),
                membership.getRole(),
                membership.getStatus(),
                membership.getJoinedAt(),
                membership.getRevokedAt(),
                membership.getRevokedBy()
        );
    }

    public record OrganisationMemberResponse(
            UUID id,
            UUID organisationId,
            UUID userId,
            OrganisationRole role,
            OrganisationMembershipStatus status,
            LocalDateTime joinedAt,
            LocalDateTime revokedAt,
            UUID revokedBy
    ) {
    }
}
