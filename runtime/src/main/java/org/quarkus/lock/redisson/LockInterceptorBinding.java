package org.quarkus.lock.redisson;

import org.quarkus.lock.redisson.model.LockType;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface LockInterceptorBinding {

    @Nonbinding
    String name() default "";

    @Nonbinding
    String[] lockKeyParameterPositions() default {};

    @Nonbinding
    LockType lockType() default LockType.Fair;

    @Nonbinding
    long waitTime() default 60;

    @Nonbinding
    long leaseTime() default 60;
}
