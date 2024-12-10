package org.zurika.zeehealth.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zurika.zeehealth.model.*;

import java.util.*;

/**
 * The UserRepository interface provides methods for interacting with the database
 * to perform CRUD operations on User entities. It extends JpaRepository to leverage
 * built-in methods and supports custom query methods for specific requirements.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(UserRole role);
    List<User> findAll(Sort sort);
    boolean existsByUsername(String username);
}

