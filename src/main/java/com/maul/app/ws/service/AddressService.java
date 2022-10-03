package com.maul.app.ws.service;

import java.util.List;

import com.maul.app.ws.shared.dto.AddressDTO;

public interface AddressService {
    List<AddressDTO> getAddresses(String userId);
}
