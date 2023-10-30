package org.quarkus.lock.redisson.deployment;

import org.jboss.jandex.DotName;
import org.quarkus.lock.redisson.Lock;
import org.quarkus.lock.redisson.LockKey;

public class DotNames {

    public static final DotName LOCK = dotName(Lock.class);
    public static final DotName LOCK_KEY = dotName(LockKey.class);

    public static final String NAME_PARAM = "name";
    public static final String LOCK_KEY_PARAMETER_POSITIONS_PARAM = "lockKeyParameterPositions";
    public static final String LOCK_TYPE_PARAM = "lockType";
    public static final String WAIT_TIME_PARAM = "waitTime";
    public static final String LEASE_TIME_PARAM = "leaseTime";
    public static final String LOCK_KEY_VALUE_PARAM = "fieldName";

    private static DotName dotName(Class<?> annotationClass) {
        return DotName.createSimple(annotationClass.getName());
    }
}
