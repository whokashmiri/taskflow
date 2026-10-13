package com.taskflow.model.task;

import com.taskflow.model.project.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "task_labels",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_label_name",
                        columnNames = {"project_id", "name"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Project owning the label
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * Label name
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Hex color code (#FF5733)
     */
    @Column(nullable = false, length = 7)
    private String color;

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

    @ManyToMany(mappedBy = "labels")
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
