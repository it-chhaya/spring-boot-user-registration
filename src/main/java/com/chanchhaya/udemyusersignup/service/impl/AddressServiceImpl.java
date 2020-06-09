package com.chanchhaya.udemyusersignup.service.impl;

import com.chanchhaya.udemyusersignup.io.entity.AddressEntity;
import com.chanchhaya.udemyusersignup.io.entity.UserEntity;
import com.chanchhaya.udemyusersignup.io.repository.AddressRepository;
import com.chanchhaya.udemyusersignup.io.repository.UserRepository;
import com.chanchhaya.udemyusersignup.service.AddressService;
import com.chanchhaya.udemyusersignup.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private UserRepository userRepository;
    private AddressRepository addressRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setAddressRepository(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDto> getAddresses(String userId) {

        List<AddressDto> returnValue = new ArrayList<>();

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) return  returnValue;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

        for (AddressEntity addressEntity : addresses) {
            returnValue.add(new ModelMapper().map(addressEntity, AddressDto.class));
        }

        System.out.println(returnValue);

        return returnValue;

    }

    @Override
    public AddressDto getAddress(String addressId) {

        AddressDto returnValue = null;
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (addressEntity != null) {
            returnValue = new ModelMapper().map(addressEntity, AddressDto.class);
        }

        return returnValue;

    }

}
