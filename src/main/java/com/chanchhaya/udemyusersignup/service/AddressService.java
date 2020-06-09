package com.chanchhaya.udemyusersignup.service;

import com.chanchhaya.udemyusersignup.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String addressId);
}
