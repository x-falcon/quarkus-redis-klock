package org.quarkus.lock.redisson.lock;

import org.quarkus.lock.redisson.model.LockInfo;
import org.redisson.api.RedissonClient;

public class LockManager {

    RedissonClient redissonClient;

    public LockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public Lock getLock(LockInfo lockInfo) {
        switch (lockInfo.getType()) {
            case NonFair:
                return new NonFairLock(redissonClient, lockInfo);
            case Read:
                return new ReadLock(redissonClient, lockInfo);
            case Write:
                return new WriteLock(redissonClient, lockInfo);
            default:
                return new FairLock(redissonClient, lockInfo);
        }
    }

}
