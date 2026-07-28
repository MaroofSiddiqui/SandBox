package com.sandbox.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sandbox.entity.Organization;

/*
 * ORGANIZATION REPOSITORY
 *
 * Purpose:
 * This interface is the database-access layer for
 * the Organization entity.
 *
 * It allows OrganizationService to communicate with
 * the "organizations" table without manually writing
 * SQL for common database operations.
 *
 * Flow:
 *
 * OrganizationController
 *          ↓
 * OrganizationService
 *          ↓
 * OrganizationRepository
 *          ↓
 * Database
 */
public interface OrganizationRepository
        extends JpaRepository<Organization, Long> {

    /*
     * JpaRepository<Organization, Long>
     *
     * Organization:
     * -> Specifies which entity this repository manages.
     *
     * Long:
     * -> Specifies the datatype of Organization's primary key.
     *
     * By extending JpaRepository, we automatically get
     * many ready-made CRUD methods such as:
     *
     * save(organization)
     * findById(id)
     * findAll()
     * delete(organization)
     * deleteById(id)
     * existsById(id)
     * count()
     *
     * We don't need to implement these methods ourselves.
     */


    /*
     * FIND ORGANIZATION BY DOMAIN
     *
     * Spring Data JPA automatically creates the query
     * based on this method's name.
     *
     * Method:
     *
     * findByDomain(String domain)
     *
     * is interpreted approximately as:
     *
     * SELECT *
     * FROM organizations
     * WHERE domain = ?
     *
     * Example:
     *
     * organizationRepository.findByDomain("acme.com");
     */


    /*
     * Optional<Organization> is used because an organization
     * with the requested domain may or may not exist.
     *
     * If found:
     *
     * Optional contains Organization
     *
     * If not found:
     *
     * Optional.empty()
     *
     * This helps avoid directly dealing with null values.
     */
    Optional<Organization> findByDomain(String domain);


    /*
     * CHECK WHETHER DOMAIN ALREADY EXISTS
     *
     * This method checks whether any organization
     * already uses the supplied domain.
     *
     * Example:
     *
     * organizationRepository.existsByDomain("acme.com");
     *
     * returns:
     *
     * true  -> domain already exists
     * false -> domain does not exist
     *
     * Spring Data JPA again generates the query
     * automatically from the method name.
     */
    boolean existsByDomain(String domain);
}