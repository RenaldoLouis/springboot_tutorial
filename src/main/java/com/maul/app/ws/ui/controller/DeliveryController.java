package com.maul.app.ws.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maul.app.ws.exceptions.UserServiceException;
import com.maul.app.ws.service.DeliveryService;
import com.maul.app.ws.shared.dto.DeliveryDTO;
import com.maul.app.ws.ui.model.request.CreateDeliveryRequestModel;
import com.maul.app.ws.ui.model.response.DeliveryRest;
import com.maul.app.ws.ui.model.response.ErrorMessages;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    DeliveryService deliveryService;

    @GetMapping("/createDelivery")
    public DeliveryRest createDelivery(@RequestBody CreateDeliveryRequestModel createDeliveryRequestModel) {
        DeliveryRest returnedValue = new DeliveryRest();

        DeliveryDTO deliveryDTO = deliveryService.createDelivery(createDeliveryRequestModel);

        if (deliveryDTO == null)
            throw new UserServiceException(ErrorMessages.CREATE_DELIVERY_FAILED.getErrorMessage());

        BeanUtils.copyProperties(deliveryDTO, returnedValue);

        return returnedValue;
    }

}
