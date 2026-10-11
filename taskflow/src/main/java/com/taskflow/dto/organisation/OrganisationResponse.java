package com.taskflow.dto.organisation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class OrganisationResponse {
    private UUID id;
    private String name;
    private String ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
