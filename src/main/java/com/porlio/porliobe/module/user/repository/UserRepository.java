package com.porlio.porliobe.module.user.repository;

import com.porlio.porliobe.module.shared.data.base.BaseRepository;
import com.porlio.porliobe.module.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
