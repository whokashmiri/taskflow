
package com.taskflow.controller.organisation;

import com.taskflow.dto.organisation.OrganisationRequest;
import com.taskflow.dto.organisation.OrganisationResponse;
import com.taskflow.dto.project.ProjectResponse;
import com.taskflow.service.organisation.OrganisationService;
import com.taskflow.service.project.ProjectService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {

    private final OrganisationService organisationService;
    private final ProjectService projectService;

    public OrganisationController(
            OrganisationService organisationService,
            ProjectService projectService
    ) {
        this.organisationService = organisationService;
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<OrganisationResponse> createOrganisation(
            @Valid @RequestBody OrganisationRequest request
    ) {
        OrganisationResponse response =
                organisationService.createOrganisation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrganisationResponse>> getMyOrganisations() {
        List<OrganisationResponse> response =
                organisationService.getOrganisation();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{organisationId}/projects")
    public ResponseEntity<List<ProjectResponse>> getProjectsByOrganisation(
            @PathVariable UUID organisationId
    ) {
        List<ProjectResponse> response =
                projectService.getProjectsByOrganisation(organisationId);

        return ResponseEntity.ok(response);
    }
}
