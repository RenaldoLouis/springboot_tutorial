package com.maul.app.ws.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.maul.app.ws.io.entity.CourierEntity;
import com.maul.app.ws.io.entity.DeliveryEntity;
import com.maul.app.ws.io.repositories.CourierRepository;
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
    CourierRepository courierRepository;

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

        // change 1 courier to not vacant

        List<CourierEntity> listOfCourier = courierRepository.findByOccupied(false);

        boolean assignCourier = false;

        for (CourierEntity courierEntity : listOfCourier) {
            if (courierEntity.isOccupied() == false && assignCourier == false) {
                courierEntity.setOccupied(true);
                courierEntity.setDeliveryCode(publicDeliveryCode);
                assignCourier = true;

                deliveryEntity.setCourier(courierEntity);
                courierRepository.save(courierEntity);
            }
        }

        // add the relation on joinTable between delivery and courier

        return returnedValue;
    }

    @Override
    public List<DeliveryDTO> getAllDelivery(int page, int limit) {
        List<DeliveryDTO> returnedValue = new ArrayList<>();

        if (page > 0)
            page -= 1;

        org.springframework.data.domain.Pageable pageableRequest = PageRequest.of(page, limit);

        Page<DeliveryEntity> deliveryPage = deliveryRepository.findAll(pageableRequest);

        List<DeliveryEntity> deliverys = deliveryPage.getContent();

        for (DeliveryEntity deliveryEntity : deliverys) {
            DeliveryDTO deliveryDTO = new DeliveryDTO();
            BeanUtils.copyProperties(deliveryEntity, deliveryDTO);
            returnedValue.add(deliveryDTO);
        }

        return returnedValue;
    }

    @Override
    public String completeDelivery(String deliveryCode) {
        String returnedvalue = "fail";

        DeliveryEntity deliveryEntity = deliveryRepository.findByDeliveryCode(deliveryCode);

        if (deliveryEntity != null && deliveryEntity.isCompleted() == false) {
            deliveryEntity.setCompleted(true);
            returnedvalue = "success";
            deliveryRepository.save(deliveryEntity);
        } else if (deliveryEntity != null && deliveryEntity.isCompleted() == true) {
            returnedvalue = "alreadyDone";
        }

        CourierEntity courierEntity = courierRepository.findByDeliveryCode(deliveryCode);

        if (courierEntity != null) {
            courierEntity.setOccupied(false);
            courierEntity.setDeliveryCode(null);
            courierRepository.save(courierEntity);
        }

        return returnedvalue;
    }

    @Override
    public String deleteDelivery(String deliveryCode) {
        String returnedvalue = "fail";

        DeliveryEntity deliveryEntity = deliveryRepository.findByDeliveryCode(deliveryCode);

        if (deliveryEntity != null && deliveryEntity.isCompleted() == true) {
            // baru apus
            returnedvalue = "success";
            deliveryRepository.delete(deliveryEntity);
        } else if (deliveryEntity != null && deliveryEntity.isCompleted() == false) {
            // the delivery not completed yet
            returnedvalue = "notDoneYet";
        }

        return returnedvalue;
    }

}
