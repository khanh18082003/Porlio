package com.porlio.porliobe.module.iam.access.permission.repository;

import com.porlio.porliobe.module.iam.access.permission.entity.Permission;
import com.porlio.porliobe.module.shared.data.base.BaseRepository;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, UUID> {

  Set<Permission> findByPermissionKeyIn(Collection<String> keys);

  @Query("SELECT p.permissionKey FROM Permission p")
  Set<String> findAllKeys();
}
