package org.example.backend.users;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Victoria Ha
 * @version 0.1.0
 * Description: this needs UserModelling but im just too..
 * @since 11/9/2026
 */

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndAuthSub(
            String provider,
            String authSub
    );
}