package com.manish.authservice.repository;

import com.manish.authservice.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    User findByUsername(String username);
}
