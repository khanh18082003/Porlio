package com.porlio.porliobe.module.shared.data.base;

import java.time.temporal.Temporal;

public interface CreatedAtAuditable<T extends Temporal> {

  T getCreatedAt();
}
