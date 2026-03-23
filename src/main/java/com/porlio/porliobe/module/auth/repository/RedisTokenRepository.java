package com.porlio.porliobe.module.auth.repository;

import com.porlio.porliobe.module.auth.entity.RedisToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {

}
