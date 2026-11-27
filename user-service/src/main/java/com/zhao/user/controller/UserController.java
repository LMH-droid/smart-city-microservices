package com.zhao.user.controller;

import com.zhao.user.entity.User;
import com.zhao.user.dao.UserRepository;
import com.zhao.user.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // 用户登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        Map<String, Object> response = new HashMap<>();
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 使用passwordEncoder验证密码
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // 生成JWT token
                String token = jwtUtil.generateToken(
                        user.getUsername(),
                        user.getId(),
                        user.getUserType().name()
                );

                response.put("success", true);
                response.put("message", "登录成功");
                response.put("token", token);
                response.put("user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "realName", user.getRealName(),
                        "userType", user.getUserType(),
                        "department", user.getDepartment(),
                        "phone", user.getPhone()
                ));
            } else {
                response.put("success", false);
                response.put("message", "用户名或密码错误");
            }
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }
        return response;
    }

    // 获取用户信息
    @GetMapping("/{username}")
    public Map<String, Object> getUserInfo(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        Map<String, Object> response = new HashMap<>();
        if (userOptional.isPresent()) {
            response.put("success", true);
            response.put("user", userOptional.get());
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
        }
        return response;
    }

    // 注册新用户
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }

        // 创建新用户对象
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        // 密码加密存储
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setUserType(User.UserType.valueOf(registerRequest.getUserType()));
        newUser.setRealName(registerRequest.getRealName());
        newUser.setPhone(registerRequest.getPhone());
        newUser.setDepartment(registerRequest.getDepartment());

        User savedUser = userRepository.save(newUser);

        // 注册后自动登录，返回token
        String token = jwtUtil.generateToken(
                savedUser.getUsername(),
                savedUser.getId(),
                savedUser.getUserType().name()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户注册成功");
        response.put("token", token);
        response.put("user", savedUser);
        return response;
    }

    // 获取所有用户
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 根据用户类型获取用户
    @GetMapping("/type/{userType}")
    public List<User> getUsersByType(@PathVariable String userType) {
        User.UserType type = User.UserType.valueOf(userType);
        return userRepository.findByUserType(type);
    }

    // 根据部门获取用户
    @GetMapping("/department/{department}")
    public List<User> getUsersByDepartment(@PathVariable String department) {
        return userRepository.findByDepartment(department);
    }

    // 更新用户信息
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }

        user.setId(id);
        // 如果密码被修改，需要加密
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户信息更新成功");
        response.put("user", updatedUser);
        return response;
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户删除成功");
            return response;
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户不存在");
            return response;
        }
    }

    // 健康检查
    @GetMapping("/health")
    public String healthCheck() {
        return "用户服务运行正常 - " + System.currentTimeMillis() + "，总用户数: " + userRepository.count();
    }

    // 请求类
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String userType;
        private String realName;
        private String phone;
        private String department;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }
}