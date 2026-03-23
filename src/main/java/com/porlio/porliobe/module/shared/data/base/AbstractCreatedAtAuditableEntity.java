package com.porlio.porliobe.module.shared.data.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AbstractCreatedAtAuditableEntity<I extends Serializable>
    extends AbstractEntity<I> implements InstantCreatedAtAuditable {

  @Serial
  private static final long serialVersionUID = 6890402585244092905L;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  protected Instant createdAt;

  @Override
  public Instant getCreatedAt() {
    return createdAt;
  }
}
