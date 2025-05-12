package com.gujo.uminity.user.repository;

import com.gujo.uminity.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);
}
