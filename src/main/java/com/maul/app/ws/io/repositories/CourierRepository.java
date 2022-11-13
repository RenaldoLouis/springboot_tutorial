package com.maul.app.ws.io.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.maul.app.ws.io.entity.CourierEntity;

public interface CourierRepository extends PagingAndSortingRepository<CourierEntity, Long> {

}
