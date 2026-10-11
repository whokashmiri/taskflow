package com.taskflow.controller.organisation;

import com.taskflow.dto.organisation.OrganisationRequest;
import com.taskflow.dto.organisation.OrganisationResponse;
import com.taskflow.model.organisation.Organisation;
import com.taskflow.service.organisation.OrganisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/organisations")
@RestController
public class OrganisationController {
    public final OrganisationService organisationService;

    public OrganisationController(OrganisationService organisationService){
        this.organisationService = organisationService;
    }

    @PostMapping
    public ResponseEntity<OrganisationResponse> createOrganisation(
    @RequestBody OrganisationRequest organisationRequest
    ){
     OrganisationResponse organisationResponse = organisationService.createOrganisation(organisationRequest);
     return  ResponseEntity.status(HttpStatus.CREATED).body(organisationResponse);
    }
}
