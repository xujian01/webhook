package com.jarry.gitlab.qywx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jarry.gitlab.util.GitLabApiUtils;
import com.jarry.gitlab.qywx.service.MessageStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xujian
 * @date 2020-10-27 11:02
 * Tag消息构建
 **/
public class TagMessageStrategy implements MessageStrategy {
    private static final String OPERATE_TYPE_ADD = "新增";
    private static final String OPERATE_TYPE_DELETE = "删除";
    @Override
    public String produceMsg(JSONObject jsonObject) {
        String operateType = OPERATE_TYPE_ADD;
        if ("0000000000000000000000000000000000000000".equals(jsonObject.getString("after"))) {
            operateType = OPERATE_TYPE_DELETE;
        }
        Integer projectId = jsonObject.getInteger("project_id");
        JSONObject prObject = jsonObject.getJSONObject("project");
        String repo = prObject.getString("name");
        String operator = jsonObject.getString("user_name");
        String tag = jsonObject.getString("ref");
        String[] tagArr = tag.split("/");
        tag = tagArr[tagArr.length-1];
        String detailUrl = prObject.getString("web_url")+"/tags/"+tag;
        String commitInfo = "";
        if (OPERATE_TYPE_ADD.equals(operateType)) {
            String newTagMsg = jsonObject.getString("message");
            JSONObject latestCommit = jsonObject.getJSONArray("commits").getJSONObject(0);
            String latestCommitMsg = latestCommit.getString("message").replaceAll("\r\n"," ");
            String latestCommitUser = latestCommit.getJSONObject("author").getString("name");
            commitInfo = "\n>Tag描述："+newTagMsg
                    +"\n>最近一次提交信息："+latestCommitMsg
                    +"\n>最近一次提交人："+latestCommitUser;
        }
        List<String> members = GitLabApiUtils.getAllProjectMembers(projectId);
        String alertUsers = members.stream().map(s -> "@"+s+" ").collect(Collectors.joining());
        String alertContent = alertUsers+"<font color=\\\"info\\\">【"+repo+"】</font>"+operator+"<font color=\\\"info\\\">"+operateType+"</font>了一个Tag！"
                +"\n>Tag名称："+tag
                +commitInfo
                +"\n>[查看详情]("+detailUrl+")";
        return alertContent;
    }
}
