package com.basic.redis_demo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @描述
 * @创建人 梁XL
 * @创建时间 2019/4/11
 */
@Component
@FeignClient(value = "service-redis")
public interface IRedisService {

    /**
     * 读取缓存
     * @param key
     * @return
     */
    @GetMapping("/redis/get")
    String get(@RequestParam("key") final String key);

    /**
     * @description 设置缓存
     * @author 梁XL
     * @date 2019/4/10
     * @param key
     * @param value
     * @return boolean
     * @throws
     */
    @GetMapping("/redis/set")
    boolean set(@RequestParam("key") final String key,@RequestParam("value") final String value);

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
    @GetMapping("/redis/setWithTime")
    boolean setWithTime(@RequestParam("key") final String key,@RequestParam("value") final String value,@RequestParam("timeOut") final long timeOut) ;


    /**
     * 更新缓存
     */
    @GetMapping("/redis/update")
    boolean getAndSet(@RequestParam("key") final String key,@RequestParam("value") final String value);

    /**
     * 删除缓存
     */
    @GetMapping("/redis/delete")
    boolean delete(@RequestParam("key") final String key);

}
