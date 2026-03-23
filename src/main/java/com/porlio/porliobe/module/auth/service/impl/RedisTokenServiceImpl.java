package com.porlio.porliobe.module.auth.service.impl;

import com.porlio.porliobe.module.auth.entity.RedisToken;
import com.porlio.porliobe.module.auth.repository.RedisTokenRepository;
import com.porlio.porliobe.module.auth.service.RedisTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "REDIS_TOKEN_SERVICE")
public class RedisTokenServiceImpl implements RedisTokenService {

  RedisTokenRepository redisTokenRepository;

  @Override
  public void save(RedisToken token) {
    redisTokenRepository.save(token);
  }

  @Override
  public RedisToken findById(String id) {
    return redisTokenRepository.findById(id).orElse(null);
  }

  @Override
  public void deleteById(String id) {
    redisTokenRepository.deleteById(id);
  }

  @Override
  public boolean existsById(String id) {
    return redisTokenRepository.existsById(id);
  }
}
