package com.porlio.porliobe.module.shared.data.base;

import java.io.Serializable;

/**
 * Represent an object that can be uniquely identified by a representative ID.
 *
 * @param <I> the type of the identifier, must be Serializable.
 */
public interface Identifiable<I extends Serializable> {

  /**
   * Gets the unique identifier of the object.
   *
   * @return the unique identifier.
   */
  I getId();
}
