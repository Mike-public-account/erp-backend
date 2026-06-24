package com.erp.common.utils;

import com.alibaba.fastjson2.JSON;
import com.erp.common.constant.CacheKey;
import com.erp.module.inventory.vo.StockWarningMsgVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockWarningUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void sendWarning(StockWarningMsgVO msg) {
        String json = JSON.toJSONString(msg);
        redisTemplate.convertAndSend(CacheKey.STOCK_WARNING_CHANNEL, json);
    }
}