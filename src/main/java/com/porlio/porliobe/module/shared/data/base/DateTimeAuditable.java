package com.porlio.porliobe.module.shared.data.base;

import java.time.temporal.Temporal;

public interface DateTimeAuditable<T1 extends Temporal, T2 extends Temporal>
    extends CreatedAtAuditable<T1> {

  T2 getUpdatedAt();

}
