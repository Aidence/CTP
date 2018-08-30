package org.rsna.ctp.custom;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisStore {

    static final Logger logger = Logger.getLogger(JedisStore.class);

    static final JedisFactory jedisfactory = JedisFactory.getInstance();
    private static JedisStore instance;

    // TODO: expire in one day for now, make this configurable via an environment variable
    static final int secondsToExpire = 24 * 60 * 60;

    public String save(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisfactory.getJedisPool().getResource();
            return jedis.setex(key, secondsToExpire, value);
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
        Jedis jedis = null;
        try {
            jedis = jedisfactory.getJedisPool().getResource();
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
