package org.rsna.ctp.custom.jedis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

class JedisFactory {

    private static JedisPool jedisPool;
    private static JedisFactory instance;
    public JedisConfig jedisConfig;
    public Boolean useRedis = false;


    public JedisFactory() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);

        // redis configuration
        String useRedisStr = System.getenv("USE_REDIS");
        if (useRedisStr != null) {
            useRedis = true;
        }
        if (useRedis) {
            jedisConfig = new JedisConfig();
            jedisPool = new JedisPool(
                    poolConfig, jedisConfig.host, jedisConfig.port, jedisConfig.timeout, jedisConfig.password,
                    jedisConfig.db);
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
