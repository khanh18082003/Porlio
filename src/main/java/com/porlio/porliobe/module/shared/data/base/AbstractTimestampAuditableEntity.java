package com.porlio.porliobe.module.shared.data.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AbstractTimestampAuditableEntity<I extends Serializable>
    extends AbstractEntity<I> implements InstantDateTimeAuditable {

  @Serial
  private static final long serialVersionUID = -6518839230570945025L;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  protected Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  protected Instant updatedAt;

  @Override
  public Instant getCreatedAt() {
    return createdAt;
  }

  @Override
  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
