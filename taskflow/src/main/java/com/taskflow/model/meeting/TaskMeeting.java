package com.taskflow.model.meeting;


import com.taskflow.model.task.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "task_meetings",
        indexes = {
                @Index(name = "idx_task_meeting_task", columnList = "task_id"),
                @Index(name = "idx_task_meeting_start", columnList = "start_at"),
                @Index(name = "idx_task_meeting_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Related task
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * Meeting title
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Purpose and details
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Planned discussion topics
     */
    @Column(columnDefinition = "TEXT")
    private String agenda;

    /**
     * Teams / Zoom / Meet link
     */
    @Column(length = 2000)
    private String meetingLink;

    /**
     * Meeting location
     */
    @Column(length = 500)
    private String location;

    /**
     * Start date/time
     */
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    /**
     * End date/time
     */
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    /**
     * IANA timezone
     * Example: Asia/Riyadh
     */
    @Column(nullable = false, length = 100)
    private String timeZone;

    /**
     * Scheduled, completed, cancelled
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;

    /**
     * Meeting notes and decisions
     */
    @Column(columnDefinition = "TEXT")
    private String meetingNotes;

    /**
     * Creator
     */
    @Column(nullable = false, updatable = false)
    private UUID createdBy;

    /**
     * Creation timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Transient
    public Long getDurationMinutes() {
        if (startAt == null || endAt == null) {
            return null;
        }

        return Duration.between(startAt, endAt).toMinutes();
    }

    public boolean isCancelled() {
        return status == MeetingStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return status == MeetingStatus.COMPLETED;
    }
}
