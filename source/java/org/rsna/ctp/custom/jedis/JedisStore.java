package org.rsna.ctp.custom.jedis;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisStore {

    static final Logger logger = Logger.getLogger(JedisStore.class);

    static final JedisFactory jedisFactory = JedisFactory.getInstance();
    private static JedisStore instance;

    public String save(String key, String value) {
        if (jedisFactory.useRedis == false) {
            logger.warn("Redis is not used. Doing nothing.");
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisFactory.getJedisPool().getResource();
            return jedis.setex(key, jedisFactory.jedisConfig.timeToExpire, value);
        } catch (JedisConnectionException ex) {
            logger.error("Unable to save key/value to redis: " + ex);
        } catch (Exception ex) {
            logger.error("Unable to save key/value to redis: " + ex);
        } finally {
            if (jedis != null) {
                jedis.close();  // this makes sure the client is released from the pool
            }
        }
        return null;
    }

    public String retrieve(String key) {
        if (jedisFactory.useRedis == false) {
            logger.warn("Redis is not used. Doing nothing.");
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisFactory.getJedisPool().getResource();
            return jedis.get(key);
        } catch (JedisConnectionException ex) {
            logger.error("Unable to retrieve value from redis: " + ex);
        } catch (Exception ex) {
            logger.error("Unable to retrieve value from redis: " + ex);
        } finally {
            if (jedis != null) {
                jedis.close();  // this makes sure the client is released from the pool
            }
        }
        return null;
    }

    public static JedisStore getInstance() {
        if (instance == null) {
            instance = new JedisStore();
        }
        return instance;
    }

}
