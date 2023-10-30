# quarkus-redisson-lock
Redis-based quarkus distributed lock extension
## Quick start
- 1、Introduce maven coordinates

```
        <dependency>
            <groupId>io.quarkiverse.lock</groupId>
            <artifactId>quarkus-redisson-lock</artifactId>
            <version>0.0.1</version>
        </dependency>
```
- 2、Add the following configuration in the application.properties file

```
#lock
quarkus.lock.redisson=true
quarkus.lock.redisson.redis.database=12

quarkus.redisson.single-server-config.address=redis://127.0.0.1:6379
quarkus.redisson.single-server-config.password=password
quarkus.redisson.threads=2
quarkus.redisson.netty-threads=4
```
By default, the switch of lock extension is turned off, and you need to use the [quarkus.lock.redisson=true] configuration to manually turn it on.

- 3、How to use

```
@Singleton
public class ServiceA {

    @Lock
    public String hello( @LockKey String name, @LockKey(fieldName = "name") User user){
        return "hello " + name;
    }
}
```

As in the code example above, the lock distributed lock quarkus extension is driven by adding annotations. @Lock indicates that a distributed lock is added to this method. The name of the lock is: (default: full class name + method name, through the name attribute Designated) + designated business Key. Use the @LockKey annotation to mark the locked business key, and try to reduce the strength of the lock while meeting business needs. If the input parameter is an object, you can use fieldName to specify an attribute value in the object to be obtained as the business Key,
The above represents the use of the name attribute value in the user object as the business key. The same business key will be locked, and different business keys will be released