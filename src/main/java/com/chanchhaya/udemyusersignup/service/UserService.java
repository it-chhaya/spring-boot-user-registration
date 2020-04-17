package com.chanchhaya.udemyusersignup.service;

import com.chanchhaya.udemyusersignup.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
}
