package com.chanchhaya.udemyusersignup.service;

import com.chanchhaya.udemyusersignup.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    List<UserDto> findAllUsers();
    UserDto getUser(String email);
}
