package com.maul.app.ws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maul.app.ws.io.entity.CourierEntity;
import com.maul.app.ws.io.repositories.CourierRepository;
import com.maul.app.ws.service.CourierService;
import com.maul.app.ws.shared.dto.CourierDTO;

@Service
public class CourierServiceImpl implements CourierService {

    @Autowired
    CourierRepository courierRepository;

    @Override
    public CourierDTO createCourier(String name) {
        CourierDTO returnedValue = new CourierDTO();

        CourierEntity courierEntity = new CourierEntity();

        courierEntity.setName(name);
        courierEntity.setVacant(false);

        CourierEntity createdCourier = courierRepository.save(courierEntity);

        BeanUtils.copyProperties(createdCourier, returnedValue);

        return returnedValue;
    }

}
