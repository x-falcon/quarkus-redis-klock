package org.quarkus.lock.redisson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.runtime.InterceptorBindings;
import jodd.util.StringUtil;
import org.quarkus.lock.redisson.model.LockInfo;
import org.quarkus.lock.redisson.model.LockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class LockInfoProviderUtils {

    private static final String LOCK_NAME_PREFIX = "x-quarkus-redisson-lock";
    public static final String LOCK_NAME_SEPARATOR = "#";

    private static final Logger logger = LoggerFactory.getLogger(LockInfoProviderUtils.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private LockInfoProviderUtils() {
    }

    public static LockInfo get(InvocationContext invocation) {
        LockInterceptorBinding lock = LockInfoProviderUtils.getLock(invocation);
        if (lock == null) {
            throw new NullPointerException("@lock lock exception, no lock instance is obtained");
        }
        LockType type = lock.lockType();

        String lockName = LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + getName(lock.name(), invocation) + buildBusinessKey(lock, invocation);
        long waitTime = lock.waitTime();
        long leaseTime = lock.leaseTime();

        if (leaseTime == -1 && logger.isWarnEnabled()) {
            logger.warn("Trying to acquire Lock({}) with no expiration, " +
                    "lock will keep prolong the lock expiration while the lock is still holding by current thread. " +
                    "This may cause dead lock in some circumstances.", lockName);
        }
        return new LockInfo(type, lockName, waitTime, leaseTime);
    }

    private static String buildBusinessKey(LockInterceptorBinding lock, InvocationContext invocation) {
        String[] keyParameterPositions = lock.lockKeyParameterPositions();
        Object[] methodParameterValues = invocation.getParameters();
        if (methodParameterValues.length == 0) {
            return "";
        } else {
            List<Object> keyElements = new ArrayList<>();
            for (String keyParameterPosition : keyParameterPositions) {
                String[] keyParameters = keyParameterPosition.split(LOCK_NAME_SEPARATOR);
                int methodParameterIndex = Integer.parseInt(keyParameters[0]);
                Object param = methodParameterValues[methodParameterIndex];
                if (keyParameters.length == 2) {
                    try {
                        String fieldName = keyParameterPosition.split(LOCK_NAME_SEPARATOR)[1];
                        JsonNode node = mapper.readTree(mapper.writeValueAsString(param)).get(fieldName);
                        if (node != null) {
                            keyElements.add(node.asText());
                        }
                    } catch (JsonProcessingException e) {
                        logger.error(e.getMessage(),e);
                    }
                } else {
                    keyElements.add(param);
                }
            }

            return StringUtil.join(keyElements, LOCK_NAME_SEPARATOR);
        }
    }

    private static String getName(String annotationName, InvocationContext invocation) {
        if (annotationName.isEmpty()) {
            return String.format("%s-%s", invocation.getTarget().getClass().getName(), invocation.getMethod().getName());
        } else {
            return annotationName;
        }
    }

    private static LockInterceptorBinding getLock(InvocationContext invocation) {
        Set<Annotation> annotations = InterceptorBindings.getInterceptorBindings(invocation);
        for (Annotation annotation : annotations) {
            if (annotation instanceof LockInterceptorBinding) {
                return (LockInterceptorBinding) annotation;
            }
        }
        return null;
    }
}
