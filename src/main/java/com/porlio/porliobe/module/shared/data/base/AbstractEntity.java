package com.porlio.porliobe.module.shared.data.base;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.proxy.HibernateProxy;

public abstract class AbstractEntity<I extends Serializable>
    implements Identifiable<I>, Serializable {

  @Serial
  private static final long serialVersionUID = 8378758369192356662L;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    var id = getId();

    // id == null -> transient entity -> always not equal
    // other == null -> obviously not equal
    // getEffectiveClass takes care of Hibernate proxies
    // obj instanceof AbstractIdentifiable<?> -> safe cast and check
    return id != null
        && obj != null
        && getEffectiveClass(this) == getEffectiveClass(obj)
        && obj instanceof AbstractEntity<?> ai
        && Objects.equals(id, ai.getId());
  }

  @Override
  public int hashCode() {
    var id = getId();
    return id == null ? getEffectiveClass(this).hashCode() : id.hashCode();
  }

  private static Class<?> getEffectiveClass(Object object) {
    return object instanceof HibernateProxy proxy
        ? proxy.getHibernateLazyInitializer().getPersistentClass()
        : object.getClass();
  }
}
