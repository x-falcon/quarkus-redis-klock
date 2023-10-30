package org.quarkus.lock.redisson.lock;

public interface Lock {

    boolean acquire();

    boolean release();
}

