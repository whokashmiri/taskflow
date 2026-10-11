package com.taskflow.model.organisation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organisations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private UUID ownerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedBy;
}
