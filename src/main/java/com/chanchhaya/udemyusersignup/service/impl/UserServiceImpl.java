package com.chanchhaya.udemyusersignup.service.impl;

import com.chanchhaya.udemyusersignup.exceptions.UserServiceException;
import com.chanchhaya.udemyusersignup.io.entity.UserEntity;
import com.chanchhaya.udemyusersignup.io.repository.UserRepository;
import com.chanchhaya.udemyusersignup.service.UserService;
import com.chanchhaya.udemyusersignup.shared.Utils;
import com.chanchhaya.udemyusersignup.shared.dto.AddressDto;
import com.chanchhaya.udemyusersignup.shared.dto.UserDto;
import com.chanchhaya.udemyusersignup.ui.model.response.ErrorMessage;
import com.chanchhaya.udemyusersignup.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.nio.file.attribute.UserPrincipalNotFoundException;
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
        if (checkStoredUserDetails != null)
            throw new RuntimeException("Record already exists");

        for (int i=0; i < user.getAddresses().size(); i++) {
            AddressDto addressDto = user.getAddresses().get(i);
            addressDto.setUserDetails(user);
            addressDto.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, addressDto);
        }

        //UserEntity userEntity = new UserEntity();
        //BeanUtils.copyProperties(user, userEntity);
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        // Set encrypted password because this column cannot be null in UserEntity class
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // Set userId because it doesn't exist in UserDetailsRequestModel
        // Using utils for generating userId for secure purpose
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        //userEntity.setEmailVerificationStatus(false);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        //BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        System.out.println(returnValue);

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
    public UserDto getUserByUserId(String userId) throws RuntimeException {

        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;

    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserDto returnValue = new UserDto();

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUser = userRepository.save(userEntity);

        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {

        List<UserDto> returnValue = new ArrayList<>();

        Pageable pageable = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageable);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public boolean verifyEmailToken(String token) {

        boolean returnValue = false;

        // Find user by token
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }
        return returnValue;
    }
}
