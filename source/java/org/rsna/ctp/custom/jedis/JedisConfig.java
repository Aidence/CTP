package org.rsna.ctp.custom.jedis;

public class JedisConfig {

    public String host = "127.0.0.1";
    public Integer port = 6379;
    public Integer db = 0;
    public String password;  // null
    public Integer timeout = 2000;
    public Integer timeToExpire = 24 * 60 * 60;  // one day

    public JedisConfig() {

        String hostStr = System.getenv("REDIS_HOST");
        if (hostStr != null) {
            host = hostStr;
        }
        String portStr = System.getenv("REDIS_PORT");
        if (portStr != null) {
            port = Integer.parseInt(portStr);
        }
        String dbStr = System.getenv("REDIS_DB");
        if (dbStr != null) {
            db = Integer.parseInt(dbStr);
        }
        password = System.getenv("REDIS_PASSWORD");
        String timeoutStr = System.getenv("REDIS_TIMEOUT");
        if (timeoutStr != null) {
            timeout = Integer.parseInt(timeoutStr);
        }
        String timeToExpireStr = System.getenv("REDIS_TIME_TO_EXPIRE");
        if (timeToExpireStr != null) {
            timeToExpire = Integer.parseInt(timeToExpireStr);
        }

    }

}
