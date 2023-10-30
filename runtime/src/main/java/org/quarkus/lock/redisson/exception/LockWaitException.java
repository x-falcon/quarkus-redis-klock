package org.quarkus.lock.redisson.exception;

public class LockWaitException extends Exception{

    public LockWaitException(String message) {
        super(message);
    }
}
