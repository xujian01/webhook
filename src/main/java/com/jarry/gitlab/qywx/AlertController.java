package com.jarry.gitlab.qywx;

import com.alibaba.fastjson.JSONObject;
import com.jarry.gitlab.qywx.service.impl.MRMessageStrategy;
import com.jarry.gitlab.qywx.service.impl.TagMessageStrategy;
import com.jarry.gitlab.qywx.service.MessageStrategy;
import com.jarry.gitlab.qywx.service.impl.MessageStrategyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xujian
 * @date 2020-10-23 19:52
 * gitlab-企业微信消息提醒
 **/
@RestController
@Slf4j
public class AlertController {
    @Value("${wechat.webhook}")
    private String weChatSendUrl;
    @PostMapping("/alert")
    public String alert(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String bodyContext = "发送成功";
        String objectKind = jsonObject.getString("object_kind");
        MessageStrategy messageStrategy = null;
        if("tag_push".equals(objectKind)) {
            messageStrategy = new TagMessageStrategy();
        } else if("merge_request".equals(objectKind)) {
            messageStrategy = new MRMessageStrategy();
        }
        MessageStrategyContext messageStrategyContext = new MessageStrategyContext(messageStrategy);
        String alertContent = messageStrategyContext.buildMessage(jsonObject);
        log.info("消息内容："+alertContent);
        String[] cmds={"curl",weChatSendUrl,"-H"
                ,"Content-Type: application/json","-d","{\"msgtype\": \"markdown\",\"markdown\": {\"content\": \""+alertContent+"\"}}"};
        ProcessBuilder process = new ProcessBuilder(cmds);
        try {
            process.start();
        } catch (Exception e) {
            bodyContext = "发送失败";
        }
        return bodyContext;
    }
}
