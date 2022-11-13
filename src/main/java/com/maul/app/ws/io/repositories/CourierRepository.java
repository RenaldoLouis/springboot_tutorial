package com.maul.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.maul.app.ws.io.entity.CourierEntity;

public interface CourierRepository extends PagingAndSortingRepository<CourierEntity, Long> {
    List<CourierEntity> findByVacant(Boolean vacant);

}
