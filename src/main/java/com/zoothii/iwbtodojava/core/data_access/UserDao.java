package com.zoothii.iwbtodojava.core.data_access;

import java.util.Optional;

import com.zoothii.iwbtodojava.core.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    boolean existsById(Long userId);
}
