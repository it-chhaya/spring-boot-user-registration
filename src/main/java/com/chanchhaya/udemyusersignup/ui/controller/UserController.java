package com.chanchhaya.udemyusersignup.ui.controller;

import com.chanchhaya.udemyusersignup.io.entity.UserEntity;
import com.chanchhaya.udemyusersignup.service.UserService;
import com.chanchhaya.udemyusersignup.shared.dto.UserDto;
import com.chanchhaya.udemyusersignup.ui.model.request.UserDetailsRequestModel;
import com.chanchhaya.udemyusersignup.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/users")    // http://localhost:8080/api/users
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<UserRest> findAllUsers() {

       List<UserDto> storedAllUsers = userService.findAllUsers();
       List<UserRest> returnUserRests = new ArrayList<>();

       UserRest userRest = null;

       for (UserDto storedUser : storedAllUsers) {
           userRest = new UserRest();
           BeanUtils.copyProperties(storedUser, userRest);
           returnUserRests.add(userRest);
       }

       return returnUserRests;

    }

    @PostMapping()
    public UserRest creatUser(@RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;

    }

    @PutMapping()
    public String updateUser() {
        return "update user was called";
    }

    @DeleteMapping()
    public String deleteUser() {
        return "delete user was called";
    }

}
