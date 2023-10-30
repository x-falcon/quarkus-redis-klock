package org.quarkus.lock.redisson.model;

public enum LockType {
    NonFair,
    Fair,
    Read,
    Write;

    LockType() {
    }

}
