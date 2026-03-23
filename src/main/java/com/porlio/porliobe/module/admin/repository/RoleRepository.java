package com.porlio.porliobe.module.admin.repository;

import com.porlio.porliobe.module.admin.entity.Role;
import com.porlio.porliobe.module.shared.data.base.BaseRepository;
import com.porlio.porliobe.module.shared.data.constant.RoleName;
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
