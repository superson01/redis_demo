package com.basic.redis_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basic.redis_demo.service.IRedisService;

/**
 * @描述
 * @创建人 梁XL
 * @创建时间 2019/4/10
 */
@RestController
public class RedisTest {

    @Autowired
    private IRedisService redisService;

    /**
     * 读取缓存
     * @param key
     * @return
     */
    @GetMapping("/get")
    public String get(String key){
        return redisService.get(key);
    }

    /**
     * @description 设置缓存
     * @author 梁XL
     * @date 2019/4/10
     * @param key
     * @param value
     * @return boolean
     * @throws
     */
    @GetMapping("/set")
    public boolean set(String key, String value){
        return redisService.set(key,value);
    }

    /**
     * @description 写入缓存带失效时间
     * @author 梁XL
     * @date 2019/4/10
     * @param key
     * @param value
     * @param timeOut
     * @return boolean
     * @throws
     */
    @GetMapping("/setWithTime")
    public boolean setWithTime(String key, String value,long timeOut) {
        return redisService.setWithTime(key,value,timeOut);
    }


    /**
     * 更新缓存
     */
    @GetMapping("/update")
    public boolean getAndSet(String key, String value){
        return redisService.getAndSet(key,value);
    }

    /**
     * 删除缓存
     */
    @GetMapping("/delete")
    public boolean delete(String key){
        return redisService.delete(key);
    }

}
