package com.taskflow.repository.organisation;

import com.taskflow.model.organisation.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {
    boolean existsByNameIgnoreCase(String name);
    List<Organisation> findAllByOwnerId(UUID ownerId);
}
