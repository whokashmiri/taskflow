package com.taskflow.model.task;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "task_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Task being discussed
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * Comment author
     */
    @Column(nullable = false, updatable = false)
    private UUID authorId;

    /**
     * Parent comment for replies
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private TaskComment parentComment;

    /**
     * Replies to this comment
     */
    @OneToMany(
            mappedBy = "parentComment",
            cascade = CascadeType.ALL,
            orphanRemoval = false
    )
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<TaskComment> replies = new ArrayList<>();

    /**
     * Comment content
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Whether the comment has been edited
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean edited = false;

    /**
     * Last edit timestamp
     */
    private LocalDateTime editedAt;

    /**
     * Soft deletion timestamp
     */
    private LocalDateTime deletedAt;

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

    public void edit(String newContent) {
        this.content = newContent;
        this.edited = true;
        this.editedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.content = "[Comment Deleted]";
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void addReply(TaskComment reply) {
        replies.add(reply);
        reply.setParentComment(this);
    }

    public void removeReply(TaskComment reply) {
        replies.remove(reply);
        reply.setParentComment(null);
    }
}
