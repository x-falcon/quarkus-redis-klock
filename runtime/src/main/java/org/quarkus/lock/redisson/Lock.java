package org.quarkus.lock.redisson;

import org.quarkus.lock.redisson.model.LockType;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@InterceptorBinding
public @interface Lock {

    String name() default "";

    LockType lockType() default LockType.Fair;

    long waitTime() default 60;

    long leaseTime() default 60;

}
