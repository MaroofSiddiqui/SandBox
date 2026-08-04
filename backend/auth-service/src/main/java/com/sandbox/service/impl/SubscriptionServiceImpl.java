package com.sandbox.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sandbox.dto.SubscriptionRequest;
import com.sandbox.entity.Subscription;
import com.sandbox.exception.ResourceNotFoundException;
import com.sandbox.repository.SubscriptionRepository;
import com.sandbox.service.SubscriptionService;

/*
 * SUBSCRIPTION SERVICE IMPLEMENTATION
 *
 * Purpose:
 * Implements all business logic related to
 * Subscription management.
 *
 * Responsibilities:
 *
 * - Create Subscription
 * - Update Subscription
 * - View All Subscriptions
 * - View Subscription By ID
 * - Activate / Deactivate Subscription
 *
 * Flow:
 *
 * SubscriptionController
 *          ↓
 * SubscriptionService
 *          ↓
 * SubscriptionServiceImpl
 *          ↓
 * SubscriptionRepository
 *          ↓
 * Database
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	/*
	 * SUBSCRIPTION REPOSITORY
	 *
	 * Used for:
	 *
	 * - Saving subscriptions - Updating subscriptions - Finding subscriptions -
	 * Checking duplicate plan names
	 */
	private final SubscriptionRepository subscriptionRepository;

	/*
	 * CONSTRUCTOR DEPENDENCY INJECTION
	 *
	 * Spring automatically injects the SubscriptionRepository.
	 */
	public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {

		this.subscriptionRepository = subscriptionRepository;
	}

	/*
	 * CREATE SUBSCRIPTION
	 *
	 * Creates a new subscription plan after validating the request.
	 *
	 * Steps:
	 *
	 * 1. Normalize the plan name. 2. Check for duplicate plan names. 3. Build the
	 * Subscription entity. 4. Save the subscription. 5. Return the saved entity.
	 */
	@Override
	public Subscription createSubscription(SubscriptionRequest request) {

		/*
		 * STEP 1: NORMALIZE PLAN NAME
		 *
		 * Removes unnecessary spaces.
		 *
		 * Example:
		 *
		 * "  Basic  "
		 *
		 * becomes
		 *
		 * "Basic"
		 */
		String planName = request.getPlanName().trim();

		/*
		 * STEP 2: CHECK FOR DUPLICATE PLAN NAME
		 *
		 * Every subscription plan should have a unique name.
		 */
		if (subscriptionRepository.existsByPlanName(planName)) {

			throw new IllegalArgumentException("Subscription plan already exists");

		}

		/*
		 * STEP 3: BUILD SUBSCRIPTION ENTITY
		 */
		Subscription subscription = Subscription.builder()

				/*
				 * Store normalized plan name.
				 */
				.planName(planName)

				/*
				 * Store description.
				 */
				.description(request.getDescription().trim())

				/*
				 * Subscription validity.
				 */
				.durationMonths(request.getDurationMonths())

				/*
				 * Plan price.
				 */
				.price(request.getPrice())

				/*
				 * Maximum candidates allowed.
				 */
				.maxCandidates(request.getMaxCandidates())

				/*
				 * New plans are ACTIVE by default.
				 */
				.status("ACTIVE")

				.build();

		/*
		 * STEP 4: SAVE SUBSCRIPTION
		 *
		 * Hibernate inserts a new row into the subscriptions table.
		 *
		 * @PrePersist automatically sets:
		 *
		 * createdAt
		 */
		return subscriptionRepository.save(subscription);

	}
	
	/*
	 * UPDATE SUBSCRIPTION
	 *
	 * Updates the details of an existing subscription plan.
	 *
	 * Steps:
	 *
	 * 1. Find the subscription using its ID.
	 * 2. Check whether the new plan name conflicts
	 *    with another existing plan.
	 * 3. Update editable fields.
	 * 4. Save the changes.
	 */
	@Override
	public Subscription updateSubscription(
	        Long id,
	        SubscriptionRequest request) {

	    /*
	     * STEP 1: FIND EXISTING SUBSCRIPTION
	     */
	    Subscription subscription =
	            subscriptionRepository
	                    .findById(id)
	                    .orElseThrow(() ->
	                            new ResourceNotFoundException(
	                                    "Subscription plan not found"
	                            )
	                    );


	    /*
	     * STEP 2: NORMALIZE PLAN NAME
	     */
	    String planName =
	            request.getPlanName().trim();


	    /*
	     * Check whether another subscription already
	     * uses this plan name.
	     *
	     * We allow the current subscription to keep
	     * its existing name.
	     */
	    subscriptionRepository
	            .findByPlanName(planName)
	            .ifPresent(existingSubscription -> {

	                if (!existingSubscription
	                        .getId()
	                        .equals(id)) {

	                    throw new IllegalArgumentException(
	                            "Subscription plan already exists"
	                    );

	                }

	            });


	    /*
	     * STEP 3: UPDATE FIELDS
	     *
	     * Notice that we DO NOT update:
	     *
	     * id
	     * status
	     * createdAt
	     *
	     * Those fields are managed separately.
	     */
	    subscription.setPlanName(planName);

	    subscription.setDescription(
	            request.getDescription().trim()
	    );

	    subscription.setDurationMonths(
	            request.getDurationMonths()
	    );

	    subscription.setPrice(
	            request.getPrice()
	    );

	    subscription.setMaxCandidates(
	            request.getMaxCandidates()
	    );


	    /*
	     * STEP 4: SAVE UPDATED SUBSCRIPTION
	     */
	    return subscriptionRepository.save(subscription);
	}


	/*
	 * GET ALL SUBSCRIPTIONS
	 *
	 * Retrieves every subscription plan stored
	 * in the database.
	 *
	 * This includes:
	 *
	 * ACTIVE plans
	 * INACTIVE plans
	 *
	 * Admin needs both because inactive plans
	 * must still be visible for management.
	 */
	@Override
	public List<Subscription> getAllSubscriptions() {

	    return subscriptionRepository.findAll();

	}


	/*
	 * GET SUBSCRIPTION BY ID
	 *
	 * Retrieves one subscription using its
	 * database ID.
	 *
	 * If the subscription does not exist,
	 * ResourceNotFoundException is thrown.
	 */
	@Override
	public Subscription getSubscriptionById(
	        Long id) {

	    return subscriptionRepository
	            .findById(id)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Subscription plan not found"
	                    )
	            );

	}


	/*
	 * UPDATE SUBSCRIPTION STATUS
	 *
	 * Used by Admin to activate or deactivate
	 * a subscription plan.
	 *
	 * Valid values:
	 *
	 * ACTIVE
	 * INACTIVE
	 *
	 * Example:
	 *
	 * ACTIVE
	 *    ↓
	 * INACTIVE
	 */
	@Override
	public Subscription updateSubscriptionStatus(
	        Long id,
	        String status) {

	    /*
	     * STEP 1: FIND SUBSCRIPTION
	     */
	    Subscription subscription =
	            subscriptionRepository
	                    .findById(id)
	                    .orElseThrow(() ->
	                            new ResourceNotFoundException(
	                                    "Subscription plan not found"
	                            )
	                    );


	    /*
	     * STEP 2: VALIDATE STATUS
	     *
	     * We do not allow arbitrary values such as:
	     *
	     * ENABLED
	     * DISABLED
	     * DELETED
	     */
	    if (status == null ||
	            (!status.equalsIgnoreCase("ACTIVE")
	                    && !status.equalsIgnoreCase("INACTIVE"))) {

	        throw new IllegalArgumentException(
	                "Status must be ACTIVE or INACTIVE"
	        );

	    }


	    /*
	     * STEP 3: NORMALIZE AND UPDATE STATUS
	     */
	    subscription.setStatus(
	            status.toUpperCase()
	    );


	    /*
	     * STEP 4: SAVE CHANGES
	     */
	    return subscriptionRepository.save(subscription);

	}

}