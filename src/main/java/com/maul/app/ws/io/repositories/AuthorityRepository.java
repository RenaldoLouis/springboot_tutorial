package com.maul.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.maul.app.ws.io.entity.AuthorityEntity;

public interface AuthorityRepository extends CrudRepository<AuthorityEntity, Long> {
    AuthorityEntity findByName(String name);
}
