package com.zomato.user_service.repository;

import com.zomato.user_service.entity.Users;
import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
Optional<Users> findUsersByUserName(String userName);

List<Users> findUsersByRoleAndStatus(Role role, Status status);

}