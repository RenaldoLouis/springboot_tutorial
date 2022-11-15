package com.maul.app.ws.io.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.maul.app.ws.io.entity.DeliveryEntity;

public interface DeliveryRepository extends PagingAndSortingRepository<DeliveryEntity, Long> {

    DeliveryEntity findByDeliveryCode(String deliveryCode);

}
