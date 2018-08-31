package org.rsna.ctp.custom;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

class JedisFactory {

    private static JedisPool jedisPool;
    private static JedisFactory instance;
    public Boolean useRedis = false;

    public JedisFactory() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);

        // redis configuration
        // N.B. [SS 20180830]: I'm sure this can be improved by putting it in a separate class, but I just want to
        // focus on getting it to work right now
        String useRedisStr = System.getenv("USE_REDIS");
        if (useRedisStr != null) {
            useRedis = true;
        }
        if (useRedis) {
            String host = System.getenv("REDIS_HOST");
            if (host == null) {
                host = "127.0.0.1";  // default
            }
            Integer port = 6379;  // default
            String portStr = System.getenv("REDIS_PORT");
            if (portStr != null) {
                port = Integer.parseInt(portStr);
            }
            Integer db = 0;  // default
            String dbStr = System.getenv("REDIS_PORT");
            if (dbStr != null) {
                db = Integer.parseInt(dbStr);
            }
            String password = System.getenv("REDIS_PASSWORD");  // default: null
            Integer timeout = 3000; // default
            String timeoutStr = System.getenv("REDIS_TIMEOUT");
            if (timeoutStr != null) {
                timeout = Integer.parseInt(timeoutStr);
            }
            jedisPool = new JedisPool(poolConfig, host, port, timeout, password, db);
        }

    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public static JedisFactory getInstance() {
        if (instance == null) {
            instance = new JedisFactory();
        }
        return instance;
    }

}
