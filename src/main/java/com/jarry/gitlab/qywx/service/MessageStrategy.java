package com.jarry.gitlab.qywx.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 构建消息内容
 */
public interface MessageStrategy {
    String produceMsg(JSONObject jsonObject);
}
