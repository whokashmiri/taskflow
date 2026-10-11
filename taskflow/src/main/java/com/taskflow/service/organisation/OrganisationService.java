package com.taskflow.service.organisation;

import com.taskflow.dto.organisation.OrganisationRequest;
import com.taskflow.dto.organisation.OrganisationResponse;
import com.taskflow.exception.auth.UserNotAuthenticatedException;
import com.taskflow.exception.organisation.OrganisationAlreadyException;
import com.taskflow.exception.organisation.OrganisationEmptyException;
import com.taskflow.model.organisation.Organisation;
import com.taskflow.model.user.User;
import com.taskflow.repository.organisation.OrganisationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganisationService {
    private final OrganisationRepository organisationRepository;

    public OrganisationService(OrganisationRepository organisationRepository){
        this.organisationRepository = organisationRepository;
    }

    public OrganisationResponse createOrganisation(OrganisationRequest organisationRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication ==  null || !(authentication.getPrincipal() instanceof User)){
            throw new UserNotAuthenticatedException("User not authenticated");
        }
      UUID ownerId =  UUID.fromString(authentication.getName());

        String name = organisationRequest.getName().trim();
        if (name.isEmpty()){
            throw new OrganisationEmptyException("Organisation name cannot be empty");
        }
        if (organisationRepository.existsByNameIgnoreCase(name)){
            throw new OrganisationAlreadyException("An organisation with name already present");
        }
        Organisation organisation = new Organisation();
        organisation.setName(organisationRequest.getName());
        organisation.setOwnerId(ownerId);
        Organisation savedOrganisation = organisationRepository.save(organisation);

        OrganisationResponse organisationResponse = new OrganisationResponse();
        organisationResponse.setId(savedOrganisation.getId());
        organisationResponse.setId(savedOrganisation.getId());
        organisationResponse.setName(savedOrganisation.getName());
        organisationResponse.setOwnerId(savedOrganisation.getOwnerId().toString());

        return organisationResponse;
    }
}
