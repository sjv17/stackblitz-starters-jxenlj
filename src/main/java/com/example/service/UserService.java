package com.example.tasko.service;

import com.example.tasko.model.Enterprise;
import com.example.tasko.model.User;
import com.example.tasko.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            throw e;
        }
    }

    public User getUserByUsername(String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                logger.error("User not found with username: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error getting user by username: {}", username, e);
            throw e;
        }
    }

    public User getUserById(Long id) {
        try {
            return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
        } catch (Exception e) {
            logger.error("Error getting user by id: {}", id, e);
            throw e;
        }
    }

    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            logger.error("Error getting all users", e);
            throw e;
        }
    }

    public List<User> getUsersByIds(List<Long> userIds) {
        try {
            return userRepository.findAllById(userIds);
        } catch (Exception e) {
            logger.error("Error getting users by ids", e);
            throw e;
        }
    }

    public long countUsersByEnterprise(Enterprise enterprise) {
        try {
            return userRepository.countByEnterprise(enterprise);
        } catch (Exception e) {
            logger.error("Error counting users by enterprise", e);
            throw e;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = getUserByUsername(username);
            return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            throw e;
        }
    }
}