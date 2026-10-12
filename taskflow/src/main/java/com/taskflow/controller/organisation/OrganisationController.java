package com.taskflow.controller.organisation;

import com.taskflow.dto.organisation.OrganisationRequest;
import com.taskflow.dto.organisation.OrganisationResponse;
import com.taskflow.model.organisation.Organisation;
import com.taskflow.service.organisation.OrganisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/me")
    public ResponseEntity<List<OrganisationResponse>> getOrganisation(){
       List<OrganisationResponse> organisationResponse =
                organisationService.getOrganisation();
        return ResponseEntity.ok(organisationResponse);
    }
}
