package com.zhao.user.dao;

import com.zhao.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByUserType(User.UserType userType);
    List<User> findByDepartment(String department);
    boolean existsByUsername(String username); // 新增：检查用户名是否存在
}