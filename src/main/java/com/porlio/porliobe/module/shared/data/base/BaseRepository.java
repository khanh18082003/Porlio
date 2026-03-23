package com.porlio.porliobe.module.shared.data.base;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends AbstractEntity<I>, I extends Serializable>
    extends JpaRepository<E, I>,
    JpaSpecificationExecutor<E> {

}
