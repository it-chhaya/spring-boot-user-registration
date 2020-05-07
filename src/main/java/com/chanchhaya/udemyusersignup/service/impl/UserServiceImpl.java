package com.chanchhaya.udemyusersignup.service.impl;

import com.chanchhaya.udemyusersignup.io.entity.UserEntity;
import com.chanchhaya.udemyusersignup.io.repository.UserRepository;
import com.chanchhaya.udemyusersignup.service.UserService;
import com.chanchhaya.udemyusersignup.shared.Utils;
import com.chanchhaya.udemyusersignup.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    Utils utils;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUtils(Utils utils) {
        this.utils = utils;
    }

    @Autowired
    public void setbCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto user) {

        UserEntity checkStoredUserDetails = userRepository.findByEmail(user.getEmail());
        if (checkStoredUserDetails != null) throw new RuntimeException("Record already exists");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        // Set encrypted password because this column cannot be null in UserEntity class
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // Set userId because it doesn't exist in UserDetailsRequestModel
        // Using utils for generating userId for secure purpose
        userEntity.setUserId(utils.generateUserId(30));

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;

    }

    @Override
    public List<UserDto> findAllUsers() {

        List<UserEntity> entities = StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        List<UserDto> returnValues = new ArrayList<>();

        UserDto dto = null;

        for (UserEntity entity: entities) {
            dto = new UserDto();
            BeanUtils.copyProperties(entity , dto);
            returnValues.add(dto);
        }

        return returnValues;

    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (ObjectUtils.isEmpty(userEntity)) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

}
