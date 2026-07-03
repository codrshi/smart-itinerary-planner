package com.codrshi.smart_itinerary_planner.repository;

import com.codrshi.smart_itinerary_planner.entity.User;
import com.codrshi.smart_itinerary_planner.util.annotation.Masked;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    List<User> findByUsernameOrEmail(String username,@Masked String email);
}
