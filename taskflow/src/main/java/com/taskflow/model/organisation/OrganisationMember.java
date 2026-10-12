package com.taskflow.model.organisation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "organisation_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_organisation_member_user",
                        columnNames = {"organisation_id", "user_id"}
                )
        }
)
public class OrganisationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrganisationRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrganisationMembershipStatus status;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by")
    private UUID revokedBy;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();

        if (status == null) {
            status = OrganisationMembershipStatus.ACTIVE;
        }
    }
}
