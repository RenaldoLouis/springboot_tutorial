package com.maul.app.ws.service.impl;

import java.sql.Date;
import java.util.Calendar;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maul.app.ws.io.entity.DeliveryEntity;
import com.maul.app.ws.io.repositories.DeliveryRepository;
import com.maul.app.ws.service.DeliveryService;
import com.maul.app.ws.shared.Utils;
import com.maul.app.ws.shared.dto.DeliveryDTO;
import com.maul.app.ws.ui.model.request.CreateDeliveryRequestModel;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    Utils utils;

    @Override
    public DeliveryDTO createDelivery(CreateDeliveryRequestModel createDeliveryRequestModel) {
        DeliveryDTO returnedValue = new DeliveryDTO();

        Date date = new java.sql.Date(Calendar.getInstance().getTimeInMillis());

        DeliveryEntity deliveryEntity = new DeliveryEntity();

        String publicDeliveryCode = utils.generateDeliveryCode(5);

        deliveryEntity.setName(createDeliveryRequestModel.getDeliveryName());
        deliveryEntity.setQuantity(createDeliveryRequestModel.getQuantity());
        deliveryEntity.setBuyerId(createDeliveryRequestModel.getUserId());
        deliveryEntity.setDeliveryTime(date);
        deliveryEntity.setDeliveryCode(publicDeliveryCode);

        DeliveryEntity createdDelivery = deliveryRepository.save(deliveryEntity);

        BeanUtils.copyProperties(createdDelivery, returnedValue);

        return returnedValue;
    }

}
