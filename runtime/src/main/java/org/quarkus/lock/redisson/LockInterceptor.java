package org.quarkus.lock.redisson;

import org.quarkus.lock.redisson.exception.LockWaitException;
import org.quarkus.lock.redisson.lock.Lock;
import org.quarkus.lock.redisson.lock.LockManager;
import org.quarkus.lock.redisson.model.LockInfo;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;


@LockInterceptorBinding
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 2)
public class LockInterceptor {

    @Inject
    LockManager lockManager;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        LockInfo lockInfo = LockInfoProviderUtils.get(context);
        Lock lock = lockManager.getLock(lockInfo);
        boolean lockRes = lock.acquire();
        Object result;
        if(lockRes){
            result = context.proceed();
            lock.release();
        }else {
            throw new LockWaitException("["+lockInfo.getName() + "]Lock wait["+lockInfo.getWaitTime()+"S]timeOut");
        }
        return result;
    }

}
