package com.edwindev.springtunes_api.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link User} entities.
 */
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);
}
