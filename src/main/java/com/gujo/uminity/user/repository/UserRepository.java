package com.gujo.uminity.user.repository;

import com.gujo.uminity.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByEmail(String email);
}
