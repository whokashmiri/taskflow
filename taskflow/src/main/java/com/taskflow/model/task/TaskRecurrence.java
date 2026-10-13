package com.taskflow.model.task;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "task_recurrences",
        indexes = {
                @Index(name = "idx_recurrence_next_occurrence", columnList = "next_occurrence_at"),
                @Index(name = "idx_recurrence_enabled", columnList = "enabled")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRecurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Template task used to generate future tasks
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_task_id", nullable = false, unique = true)
    private Task templateTask;

    /**
     * DAILY, WEEKLY, MONTHLY, YEARLY, etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceFrequency frequency;

    /**
     * Every N frequency units
     * Example:
     * 1 = every week
     * 2 = every two weeks
     */
    @Column(name = "recurrence_interval", nullable = false)
    private Integer interval;

    /**
     * Weekly recurrence days
     * Example:
     * MONDAY,TUESDAY,FRIDAY
     *
     * Alternative:
     * Use JSON column instead
     */
    @Column(columnDefinition = "TEXT")
    private String daysOfWeek;

    /**
     * Monthly recurrence
     * Example: 15 => every month on 15th
     */
    private Integer dayOfMonth;

    /**
     * Recurrence start date
     */
    @Column(nullable = false)
    private LocalDateTime startAt;

    /**
     * Optional end date
     */
    private LocalDateTime endAt;

    /**
     * Example:
     * Asia/Riyadh
     * Europe/London
     */
    @Column(nullable = false, length = 100)
    private String timeZone;

    /**
     * Next scheduled occurrence
     */
    @Column(name = "next_occurrence_at", nullable = false)
    private LocalDateTime nextOccurrenceAt;

    /**
     * Whether scheduler should process this recurrence
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

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
    public boolean isActive() {
        return enabled &&
                (endAt == null || endAt.isAfter(LocalDateTime.now()));
    }
}
