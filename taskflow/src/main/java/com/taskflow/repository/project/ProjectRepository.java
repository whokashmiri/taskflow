package com.taskflow.repository.project;

import com.taskflow.model.organisation.Organisation;
import com.taskflow.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project , UUID> {
    boolean existsByNameIgnoreCaseAndOrganisation_Id(String name , UUID organisationId);
    List<Project> findAllByOrganisation_Id(UUID organisationId);
}
