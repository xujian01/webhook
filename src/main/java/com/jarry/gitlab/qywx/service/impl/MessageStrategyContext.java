package com.jarry.gitlab.qywx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jarry.gitlab.qywx.service.MessageStrategy;

/**
 * @author xujian
 * @date 2020-10-27 11:11
 **/
public class MessageStrategyContext {
    private MessageStrategy strategy;

    public MessageStrategyContext(MessageStrategy strategy) {
        this.strategy = strategy;
    }
    public String buildMessage(JSONObject jsonObject) {
        if (strategy == null) return "";
        return strategy.produceMsg(jsonObject);
    }
}
