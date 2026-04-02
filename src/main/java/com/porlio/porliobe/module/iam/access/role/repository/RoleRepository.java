package com.porlio.porliobe.module.iam.access.role.repository;

import com.porlio.porliobe.module.iam.access.role.entity.Role;
import com.porlio.porliobe.module.shared.data.base.BaseRepository;
import com.porlio.porliobe.module.iam.access.role.constant.RoleName;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseRepository<Role, UUID> {


  Optional<Role> findByRoleKey(RoleName roleKey);

  Set<Role> findByRoleKeyIn(Collection<RoleName> roleKey);
}
