package com.jarry.gitlab.qywx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jarry.gitlab.util.GitLabApiUtils;
import com.jarry.gitlab.qywx.service.MessageStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xujian
 * @date 2020-10-27 11:04
 * Merge Request消息构建
 **/
public class MRMessageStrategy implements MessageStrategy {
    @Override
    public String produceMsg(JSONObject jsonObject) {
        String operator = jsonObject.getJSONObject("user").getString("name");
        JSONObject objectAttributes = jsonObject.getJSONObject("object_attributes");
        String operateType = "变更";
        String state = objectAttributes.getString("state");
        if ("closed".equals(state)) {
            operateType = "关闭";
        } else if ("opened".equals(state)) {
            operateType = "新增";
        } else if ("merged".equals(state)) {
            operateType = "审核通过";
        }
        String source = objectAttributes.getString("source_branch");
        String target = objectAttributes.getString("target_branch");
        Integer projectId = objectAttributes.getInteger("target_project_id");
        String title = objectAttributes.getString("title");
        String description = objectAttributes.getString("description");
        JSONObject lastCommit = objectAttributes.getJSONObject("last_commit");
        String lastCommitMsg = lastCommit.getString("message").replaceAll("\n","");
        String lastCommitUser = lastCommit.getJSONObject("author").getString("name");
        String repo = objectAttributes.getJSONObject("target").getString("name");
        String url = objectAttributes.getString("url");
        List<String> members = GitLabApiUtils.getAllProjectMembers(projectId);
        String alertUsers = members.stream().map(s -> "@"+s+" ").collect(Collectors.joining());
        String alertContent = alertUsers+"<font color=\\\"info\\\">【"+repo+"】</font>"+operator+"<font color=\\\"info\\\">"+operateType+"</font>了一个Merge Request！"
                +"\n>标题："+title
                +"\n>描述："+description
                +"\n>Source Branch："+source
                +"\n>Target Branch："+target
                +"\n>最近一次提交信息："+lastCommitMsg
                +"\n>最近一次提交人："+lastCommitUser
                +"\n>[查看详情]("+url+")";
        return alertContent;
    }
}
