package com.sandbox.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.sandbox.dto.OrganizationRequest;
import com.sandbox.entity.Organization;
import com.sandbox.entity.User;
import com.sandbox.repository.OrganizationRepository;
import com.sandbox.exception.ResourceNotFoundException;

@Service
public class OrganizationService {

	private final OrganizationRepository organizationRepository;

	public OrganizationService(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public Organization createOrganization(OrganizationRequest request, User currentUser) {

		String domain = request.getDomain();

		if (domain != null && !domain.isBlank()) {
			domain = domain.trim().toLowerCase();

			if (organizationRepository.existsByDomain(domain)) {
				throw new IllegalArgumentException("Organization domain already exists");
			}
		} else {
			domain = null;
		}

		Organization organization = Organization.builder().name(request.getName().trim()).domain(domain)
				.status("ACTIVE").createdBy(currentUser.getId()).build();

		return organizationRepository.save(organization);
	}

	public List<Organization> getAllOrganizations() {
		return organizationRepository.findAll();
	}

	public Organization getOrganizationById(Long id) {
		return organizationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
	}

	public Organization updateOrganization(Long id, OrganizationRequest request) {

		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

		String domain = request.getDomain().trim().toLowerCase();

		organizationRepository.findByDomain(domain).filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new IllegalArgumentException("Organization domain already exists");
				});

		organization.setName(request.getName().trim());
		organization.setDomain(domain);

		return organizationRepository.save(organization);
	}
	
	public void deleteOrganization(Long id) {

	    Organization organization = organizationRepository.findById(id)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Organization not found"));

	    organizationRepository.delete(organization);
	}
	
	public Organization updateOrganizationStatus(
	        Long id,
	        String status) {

	    Organization organization = organizationRepository.findById(id)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Organization not found"));

	    organization.setStatus(status);

	    return organizationRepository.save(organization);
	}
}