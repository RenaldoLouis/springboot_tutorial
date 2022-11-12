package com.maul.app.ws.service;

import java.util.List;

import com.maul.app.ws.shared.dto.DeliveryDTO;
import com.maul.app.ws.ui.model.request.CreateDeliveryRequestModel;

public interface DeliveryService {

    DeliveryDTO createDelivery(CreateDeliveryRequestModel createDeliveryRequestModel);

    List<DeliveryDTO> getAllDelivery(int page, int limit);
}
