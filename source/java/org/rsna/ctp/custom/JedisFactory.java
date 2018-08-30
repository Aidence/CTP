package org.rsna.ctp.custom;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

class JedisFactory {

    private static JedisPool jedisPool;
    private static JedisFactory instance;

    public JedisFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);  // TODO: make this configurable?
        // TODO: get redis connection details (host, port, timeout, password) from environment variables (I think the latter)
        jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379, 3000, null, 0);
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
