package xyz.wjsay.mywebchat.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    @SuppressWarnings("unchecked")
    public <T> T get(BasePrefix prefix, String key, Class<T> clazz) {
        if (key == null)  return null;
        try (Jedis jedis = jedisPool.getResource()) {
            String val = jedis.get(prefix.getPrefix() + key);
            if (val == null || val.length() <= 0 || clazz == null) {
                return null;
            }            if (clazz == String.class) {
                return (T) clazz;
            } else if (clazz == int.class || clazz == Integer.class) {
                return (T)Integer.valueOf(val);
            } else if (clazz == long.class || clazz == Long.class) {
                return (T)Long.valueOf(val);
            } else {
                return JSON.toJavaObject(JSON.parseObject(val), clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> boolean set(BasePrefix prefix, String key, T value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String val = null;
            if (value == null) {
                return false;
            }
            Class clazz = value.getClass();
            if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class) {
                val =  "" + value;
            } else if (clazz == String.class) {
                val = (String)value;
            } else {
                val = JSON.toJSONString(value);
            }
            if (prefix.getExpireSeconds() <= 0) {
                jedis.set(prefix.getPrefix() + key, val);
            } else {
                jedis.setex(prefix.getPrefix() + key, prefix.getExpireSeconds(), val);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean del(BasePrefix prefix, String key) {
        if (key == null) {
            return false;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(prefix.getPrefix() + key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
