package com.sandbox.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sandbox.entity.Role;
import com.sandbox.entity.User;
import com.sandbox.repository.RoleRepository;
import com.sandbox.repository.UserRepository;

/*
 * DATA INITIALIZER
 *
 * Purpose:
 * This class automatically inserts essential/default data into the database
 * when the Spring Boot application starts.
 *
 * In our application it ensures that:
 * 1. SUPER_ADMIN role exists
 * 2. HR role exists
 * 3. CANDIDATE role exists
 * 4. A default Super Admin account exists
 */
@Component
public class DataInitializer implements CommandLineRunner {

    /*
     * Repositories are used to communicate with the database.
     *
     * RoleRepository -> operations on roles table
     * UserRepository -> operations on users table
     */
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    /*
     * Used to securely hash passwords before storing them.
     * We never store the actual/plain-text password in the database.
     */
    private final PasswordEncoder passwordEncoder;


    /*
     * Constructor Dependency Injection.
     *
     * Spring automatically provides these dependencies when
     * it creates the DataInitializer object.
     */
    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /*
     * CommandLineRunner's run() method is automatically executed
     * once after the Spring Boot application has started.
     *
     * Therefore, this is where we initialize our default database data.
     */
    @Override
    public void run(String... args) {

        // Make sure all required application roles exist.
        createRoleIfNotExists("SUPER_ADMIN");
        createRoleIfNotExists("HR");
        createRoleIfNotExists("CANDIDATE");

        // Make sure the system has a default Super Admin account.
        createSuperAdmin();
    }


    /*
     * Creates a role only if it does not already exist.
     *
     * This prevents duplicate roles from being inserted every
     * time the application restarts.
     */
    private void createRoleIfNotExists(String roleName) {

        // findByName() returns Optional<Role>.
        // isEmpty() means the role was not found in the database.
        if (roleRepository.findByName(roleName).isEmpty()) {

            // Builder pattern is used to create the Role object.
            Role role = Role.builder()
                    .name(roleName)
                    .build();

            // INSERT the new role into the database.
            roleRepository.save(role);
        }
    }


    /*
     * Creates the default Super Admin account.
     *
     * The Super Admin is the highest-level user and is not
     * associated with any particular organization.
     */
    private void createSuperAdmin() {

        String adminEmail = "admin@sandbox.com";

        /*
         * Check whether the admin already exists.
         *
         * This prevents creating another admin whenever
         * the application restarts.
         */
        if (!userRepository.existsByEmail(adminEmail)) {

            /*
             * Fetch the SUPER_ADMIN role from the database.
             *
             * orElseThrow() throws an exception if the role does not exist.
             * Normally this won't happen because SUPER_ADMIN is created
             * before createSuperAdmin() is called.
             */
            Role superAdminRole = roleRepository
                    .findByName("SUPER_ADMIN")
                    .orElseThrow();

            /*
             * Create the default admin user.
             */
            User admin = User.builder()
                    .name("Super Admin")
                    .email(adminEmail)

                    // Encode/hash the password before storing it.
                    .passwordHash(passwordEncoder.encode("Admin@123"))

                    // Assign SUPER_ADMIN role.
                    .role(superAdminRole)

                    /*
                     * Super Admin belongs to the whole system,
                     * not to a specific organization.
                     */
                    .organization(null)

                    // Account is enabled/active.
                    .status("ACTIVE")
                    .build();

            // Save the Super Admin into the users table.
            userRepository.save(admin);
        }
    }
}