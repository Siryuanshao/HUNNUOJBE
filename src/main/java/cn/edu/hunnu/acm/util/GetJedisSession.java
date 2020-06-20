package cn.edu.hunnu.acm.util;

import org.apache.ibatis.io.Resources;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetJedisSession {
    private volatile static JedisPool jedisPool = null;

    private static JedisPool getJedisSessionFactory() {
        if(jedisPool == null) {
            synchronized (GetJedisSession.class) {
                if (jedisPool == null) {
                    String resource = "redis.properties";
                    Properties properties = new Properties();
                    InputStream inputStream = null;
                    try {
                        inputStream = Resources.getResourceAsStream(resource);
                        properties.load(inputStream);
                        JedisPoolConfig poolConfig = new JedisPoolConfig();
                        poolConfig.setMaxTotal(Integer.parseInt(properties.getProperty("maxTotal")));
                        poolConfig.setMaxIdle(Integer.parseInt(properties.getProperty("maxIdle")));
                        poolConfig.setMaxWaitMillis(Integer.parseInt(properties.getProperty("maxWaitMillis")));
                        poolConfig.setTestOnBorrow(true);

                        jedisPool = new JedisPool(poolConfig, properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return jedisPool;
    }

    public static Jedis getJedisSession() {
        return getJedisSessionFactory().getResource();
    }
}
