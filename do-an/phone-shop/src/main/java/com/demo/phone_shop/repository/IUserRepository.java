package com.demo.phone_shop.repository;

import com.demo.phone_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    //User findById(Long id);
    Optional<User> findById(Long id);
}
