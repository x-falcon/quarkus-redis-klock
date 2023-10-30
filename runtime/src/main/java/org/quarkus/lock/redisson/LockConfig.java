package org.quarkus.lock.redisson;


import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

import java.util.OptionalInt;


@ConfigRoot(name = "lock.redisson", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class LockConfig {

    /**
     * Whether the lock should be enabled.
     */
    @ConfigItem(name = ConfigItem.PARENT, defaultValue = "false")
    public boolean enabled;

    /**
     * The database to use for the lock.
     */
    @ConfigItem(defaultValue = "0")
    public OptionalInt database;

}
