package com.porlio.porliobe.module.iam.user.repository;

import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.shared.data.base.BaseRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
