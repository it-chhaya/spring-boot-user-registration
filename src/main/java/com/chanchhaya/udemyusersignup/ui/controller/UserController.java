package com.chanchhaya.udemyusersignup.ui.controller;

import com.chanchhaya.udemyusersignup.exceptions.UserServiceException;
import com.chanchhaya.udemyusersignup.io.entity.UserEntity;
import com.chanchhaya.udemyusersignup.service.AddressService;
import com.chanchhaya.udemyusersignup.service.UserService;
import com.chanchhaya.udemyusersignup.shared.dto.AddressDto;
import com.chanchhaya.udemyusersignup.shared.dto.UserDto;
import com.chanchhaya.udemyusersignup.ui.model.request.UserDetailsRequestModel;
import com.chanchhaya.udemyusersignup.ui.model.response.AddressRest;
import com.chanchhaya.udemyusersignup.ui.model.response.ErrorMessages;
import com.chanchhaya.udemyusersignup.ui.model.response.OperationStatusModel;
import com.chanchhaya.udemyusersignup.ui.model.response.UserRest;
import com.chanchhaya.udemyusersignup.utils.enums.RequestOperationName;
import com.chanchhaya.udemyusersignup.utils.enums.RequestOperationStatus;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/users")    // http://localhost:8080/api/users
public class UserController {

    private UserService userService;
    private AddressService addressService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    /* @GetMapping(path = "/{userId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest getUser(@PathVariable String userId) {

        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(userId);

        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;

    }*/

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE}
    )
    public UserRest creatUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        /* Use ModelMapper instead */
        //UserDto userDto = new UserDto();
        //BeanUtils.copyProperties(userDetails, userDto);

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        //BeanUtils.copyProperties(createdUser, returnValue);
        UserRest returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;

    }

    @PutMapping(path = "/{userId}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest updateUser(@PathVariable String userId, @RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails, userDto);
        UserDto updatedUser = userService.updateUser(userId, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel deleteUser(@PathVariable String userId) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(userId);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {

        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        UserRest userRest;

        for (UserDto userDto : users) {
            userRest = new UserRest();
            BeanUtils.copyProperties(userDto, userRest);
            returnValue.add(userRest);
        }

        return returnValue;
    }

    // http://localhost:8080/api/users/{userId}/addresses
    @GetMapping(path = "/{userId}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AddressRest> getUserAddresses(@PathVariable("userId") String userId) {
        List<AddressRest> returnValue = new ArrayList<>();
        List<AddressDto> addressDto = addressService.getAddresses(userId);

        if (addressDto != null && !addressDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {
            }.getType();
            returnValue = new ModelMapper().map(addressDto, listType);
        }

        return returnValue;
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AddressRest getUserAddress(@PathVariable String addressId) {
        AddressDto addressDto = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(addressDto, AddressRest.class);
    }

    // http://localhost:8080/api/users/email-verification?token=qwerasdfzxcv
    @GetMapping(path = "/email-verification", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;

    }

}
