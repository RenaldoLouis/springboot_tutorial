package com.maul.app.ws.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maul.app.ws.exceptions.UserServiceException;
import com.maul.app.ws.service.CourierService;
import com.maul.app.ws.shared.dto.CourierDTO;
import com.maul.app.ws.ui.model.request.CourierRequestModel;
import com.maul.app.ws.ui.model.response.CourierRest;
import com.maul.app.ws.ui.model.response.ErrorMessages;

@RestController
@RequestMapping("/courier")
public class CourierController {

    @Autowired
    CourierService courierService;

    @PostMapping("/createCourier")
    public CourierRest createCourier(@RequestBody CourierRequestModel courierRequestModel) {
        CourierRest returnedValue = new CourierRest();

        CourierDTO courierDTO = courierService.createCourier(courierRequestModel.getName());

        if (courierDTO == null)
            throw new UserServiceException(ErrorMessages.CREATE_COURIER_FAILED.getErrorMessage());

        BeanUtils.copyProperties(courierDTO, returnedValue);

        return returnedValue;
    }
}
