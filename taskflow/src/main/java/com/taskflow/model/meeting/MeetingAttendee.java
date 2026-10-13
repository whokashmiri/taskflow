package com.taskflow.model.meeting;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "meeting_attendees",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_meeting_attendee",
                        columnNames = {
                                "meeting_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_meeting_attendee_meeting", columnList = "meeting_id"),
                @Index(name = "idx_meeting_attendee_user", columnList = "user_id"),
                @Index(name = "idx_meeting_attendee_status", columnList = "response_status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingAttendee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Associated meeting
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private TaskMeeting meeting;

    /**
     * Invited user
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * User who sent the invitation
     */
    @Column(name = "invited_by", nullable = false)
    private UUID invitedBy;

    /**
     * Invitation response
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "response_status", nullable = false)
    @Builder.Default
    private MeetingResponseStatus responseStatus =
            MeetingResponseStatus.PENDING;

    /**
     * Invitation timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime invitedAt;

    /**
     * Response timestamp
     */
    private LocalDateTime respondedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.invitedAt = LocalDateTime.now();
    }

    public void accept() {
        this.responseStatus = MeetingResponseStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void decline() {
        this.responseStatus = MeetingResponseStatus.DECLINED;
        this.respondedAt = LocalDateTime.now();
    }

    public void markTentative() {
        this.responseStatus = MeetingResponseStatus.TENTATIVE;
        this.respondedAt = LocalDateTime.now();
    }
}