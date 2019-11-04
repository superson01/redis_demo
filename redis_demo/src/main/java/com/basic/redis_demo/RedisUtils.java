package com.basic.redis_demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public class RedisUtils {
	
	 /**日志记录器*/
    Logger log = Logger.getLogger(RedisUtils.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ===============统一操作============

    /**
     * @param key
     * @param time
     * @return boolean
     * @throws
     * @description 指定缓存失效时间
     * @author 梁XL
     * @date 2019/4/15
     */
    @PostMapping("/expire")
    public boolean expire(@RequestParam("key") String key,@RequestParam("time") long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * @param key
     * @return boolean
     * @throws
     * @description 判断key是否存在
     * @author 梁XL
     * @date 2019/4/15
     */
    @PostMapping("/hasKey")
    public boolean hasKey(@RequestParam("key") String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key
     * @return void
     * @throws
     * @description 删除一个或多个键
     * @author 梁XL
     * @date 2019/4/15
     */
    @PostMapping("/delete")
    public void delete(@RequestBody String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }

    }

    // ==================String================

    /**
     * @param key
     * @return java.lang.String
     * @throws
     * @description 获取值
     * @author 梁XL
     * @date 2019/4/15
     */
    @PostMapping("/getStr")
    public String getStr(@RequestParam("key") final String key) {
        if (key==null){
            return  null;
        }
        String value=String.valueOf(redisTemplate.opsForValue().get(key));
        return value;
    }

    /**
     * @param key
     * @param value
     * @return boolean
     * @throws
     * @description 设置缓存
     * @author 梁XL
     * @date 2019/4/10
     */
    @PostMapping("/setStr")
    public boolean setStr(@RequestParam("key") final String key,@RequestParam("value") final String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @param value
     * @param timeOut
     * @return boolean
     * @throws
     * @description 写入缓存带失效时间
     * @author 梁XL
     * @date 2019/4/10
     */
    @PostMapping("/setStrWithTime")
    public boolean setStrWithTime(@RequestParam("key") final String key,@RequestParam("value") final String value,@RequestParam("timeOut") final long timeOut) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, timeOut, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 更新缓存
     */
    @PostMapping("/updateStr")
    public boolean getAndSetStr(@RequestParam("key") final String key,@RequestParam("value") final String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().getAndSet(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // =========================map====================

    /**
     * @description hashGet 获取单条
     * @author 梁XL
     * @date 2019/4/15
     * @param key
     * @param item
     * @return java.lang.Object
     * @throws
     */
    @PostMapping("/hget")
    public Object hget(@RequestParam("key") String key,@RequestParam("item") String item) {
        String result =String.valueOf(redisTemplate.opsForHash().get(key, item));
        return result;
    }

    /**
     * @description 获取hashKey对应的所有键值
     * @author 梁XL
     * @date 2019/4/15
     * @param key
     * @return java.util.Map<java.lang.Object,java.lang.Object>
     * @throws
     */
    @PostMapping("/hmget")
    public Map<String, Object> hmget(@RequestParam("key") String key) {
        Map<String,Object> resultMap = new HashMap<String,Object>();

        Map<Object, Object> redisMap = redisTemplate.opsForHash().entries(key);
        Set<Map.Entry<Object, Object>> entries = redisMap.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            String newKey = String .valueOf(entry.getKey());
            Object value = entry.getValue();
            resultMap.put(newKey , value);
        }

        return resultMap;
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    @PostMapping("/hmset")
    public boolean hmset(@RequestParam("key") String key,@RequestBody Map<Object, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    @PostMapping("/hmsetWithTime")
    public boolean hmsetWithTime(@RequestParam("key") String key,@RequestBody Map<String, Object> map,@RequestParam("time") long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    @PostMapping("/hset")
    public boolean hset(@RequestParam("key") String key,@RequestParam("item") String item,@RequestParam("value") Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    @PostMapping("/hsetWithTime")
    public boolean hsetWithTime(@RequestParam("key") String key,@RequestParam("item") String item,@RequestParam("value") Object value,@RequestParam("time") long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    @PostMapping("/hdel")
    public void hdel(@RequestParam("key") String key,@RequestBody Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    @PostMapping("/hasHashKey")
    public boolean hasHasKey(@RequestParam("key") String key,@RequestParam("item") String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    @PostMapping("/hincr")
    public double hincr(@RequestParam("key") String key,@RequestParam("item") String item,@RequestParam("by") double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    @PostMapping("/hdecr")
    public double hdecr(@RequestParam("key") String key,@RequestParam("item") String item,@RequestParam("by") double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }
    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    @PostMapping("/sGet")
    public Set<Object> sGet(@RequestParam("key") String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    @PostMapping("/sHasKey")
    public boolean sHasKey(@RequestParam("key") String key,@RequestParam("value") Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    @PostMapping("/sSet")
    public long sSet(@RequestParam("key") String key,@RequestBody Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    @PostMapping("/sSetAndTime")
    public long sSetAndTime(@RequestParam("key") String key,@RequestParam("time") long time,@RequestBody Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    @PostMapping("/sGetSetSize")
    public long sGetSetSize(@RequestParam("key") String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    @PostMapping("/setRemove")
    public long setRemove(@RequestParam("key") String key,@RequestBody Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
    /**
     * 移除值为value的
     *
     * @param key    键
     * @param value 值
     * @return
     */
    @PostMapping("/Srem")
    public Boolean Srem(@RequestParam("key") String key,@RequestParam("value") String value) {
        boolean result  = false;
        try {
             Object objectValue= value;
             redisTemplate.opsForSet().remove(key, objectValue);
            result=true;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("key:"+key+"===="+"value:"+value+"移除失败");
            return result;
        }

    }

    /**
     * @param key，value
     * @return boolean
     * @throws
     * @description 判断 member 元素是否是集合 key 的成员
     * @author 吴杰峰
     * @date 2019/4/16
     */
    @PostMapping("/isMember")
    public boolean isMember(@RequestParam("key") String key,@RequestParam("value") String value) {
        try {
            return redisTemplate.opsForSet().isMember(key,value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key，value
     * @return boolean
     * @throws
     * @description 向redis集合(Set)中，添加一个元素
     * @author 吴杰峰
     * @date 2019/4/16
     */
    @PostMapping("/addMember")
    public boolean addMember(@RequestParam("key") String key,@RequestParam("memeber") String memeber) {
        try {
            redisTemplate.opsForSet().add(key,memeber);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis Set结合添加元素发生异常,key:"+key+",members:"+memeber+",ErrMsg:"+e.getMessage());
            return false;
        }
    }

    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0到 -1代表所有值
     * @return
     */
    @PostMapping("/lGet")
    public List<String> lGet(@RequestParam("key") String key,@RequestParam("start") long start,@RequestParam("end") long end) {
        try {
            List<Object> range = redisTemplate.opsForList().range(key, start, end);
            List<String> resultList = new ArrayList<String>();
            for (int i = 0; i < range.size(); i++) {
                String item = String.valueOf(range.get(i));
                resultList.add(item);
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    @PostMapping("/lGetListSize")
    public long lGetListSize(@RequestParam("key")String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    @PostMapping("/lGetIndex")
    public Object lGetIndex(@RequestParam("key") String key,@RequestParam("index") long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    @PostMapping("/lSet")
    public boolean lSet(@RequestParam("key") String key,@RequestParam("value") Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    @PostMapping("/lSetWithTime")
    public boolean lSetWithTime(@RequestParam("key") String key,@RequestParam("value") Object value,@RequestParam("time") long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    @PostMapping("/lSetList")
    public boolean lSet(@RequestParam("key")String key,@RequestBody List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    @PostMapping("/lSetListWithTime")
    public boolean lSetList(@RequestParam("key")String key,@RequestBody List<Object> value,@RequestParam("time") long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    @PostMapping("/lUpdateIndex")
    public boolean lUpdateIndex(@RequestParam("key") String key,@RequestParam("index") long index,@RequestParam("value") Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    @PostMapping("/lRemove")
    public long lRemove(@RequestParam("key") String key,@RequestParam("count") long count,@RequestParam("value") Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key,count,value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
