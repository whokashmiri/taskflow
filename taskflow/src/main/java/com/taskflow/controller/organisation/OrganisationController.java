package com.taskflow.controller.organisation;

import com.taskflow.dto.organisation.OrganisationRequest;
import com.taskflow.dto.organisation.OrganisationResponse;
import com.taskflow.service.organisation.OrganisationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {

    private final OrganisationService organisationService;

    public OrganisationController(
            OrganisationService organisationService
    ) {
        this.organisationService = organisationService;
    }

    @PostMapping
    public ResponseEntity<OrganisationResponse> createOrganisation(
            @Valid @RequestBody OrganisationRequest organisationRequest
    ) {
        OrganisationResponse response =
                organisationService.createOrganisation(organisationRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrganisationResponse>> getOrganisation() {

        List<OrganisationResponse> response =
                organisationService.getOrganisation();

        return ResponseEntity.ok(response);
    }
}