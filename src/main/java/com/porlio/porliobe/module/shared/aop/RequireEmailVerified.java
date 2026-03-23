package com.porlio.porliobe.module.shared.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireEmailVerified {

  String message() default "MESSAGE_EMAIL_NOT_VERIFIED";
}
