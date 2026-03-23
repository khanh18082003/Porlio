package com.porlio.porliobe.module.auth.entity;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("redis_token")
public class RedisToken implements Serializable {

  @Id
  String id;

  String tokenType;

  @TimeToLive(unit = TimeUnit.SECONDS)
  Long expiredTime;
}
