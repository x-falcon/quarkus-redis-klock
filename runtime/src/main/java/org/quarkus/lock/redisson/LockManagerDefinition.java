package org.quarkus.lock.redisson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.quarkus.lock.redisson.lock.LockManager;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;


@ApplicationScoped
public class LockManagerDefinition {

    private static final Logger logger = LoggerFactory.getLogger(LockManagerDefinition.class);

    @Produces
    @Singleton
    @DefaultBean
    public LockManager create(RedissonClient redissonClient, LockConfig lockConfig) {
        try {
            String yamlConfig =redissonClient.getConfig().toYAML().replaceAll("database: \\d+","database: "+lockConfig.database.getAsInt());
            RedissonClient lockRedissonClient = Redisson.create(Config.fromYAML(yamlConfig));
            logger.info("Create RedissonClient For Quarkus Distributed Lock With DB {}", lockConfig.database);
            return new LockManager(lockRedissonClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
