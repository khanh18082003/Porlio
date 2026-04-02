package com.porlio.porliobe.module.iam.session.service;

import com.porlio.porliobe.module.iam.session.entity.RedisToken;

public interface RedisTokenService {

  void save(RedisToken token);

  RedisToken findById(String id);

  void deleteById(String id);

  boolean existsById(String id);
}
