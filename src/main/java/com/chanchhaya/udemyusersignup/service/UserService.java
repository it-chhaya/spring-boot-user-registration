package com.chanchhaya.udemyusersignup.service;

import com.chanchhaya.udemyusersignup.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto updateUser(String userId, UserDto user);
    List<UserDto> findAllUsers();
    UserDto getUser(String email);
    UserDto getUserByUserId(String userId);
    void deleteUser(String userId);
    List<UserDto> getUsers(int page, int limit);
}
