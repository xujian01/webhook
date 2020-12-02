package com.jarry.gitlab.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xujian
 * @date 2020-10-30 11:31
 **/
@Slf4j
@Component
public class GitLabApiUtils {
    private static String getProjectMembersUrl;
    private static String getGroupMembersUrl;
    private static String token;

    @Value("${project.members.api.url}")
    public void setGetProjectMembersUrl(String getProjectMembersUrl) {
        GitLabApiUtils.getProjectMembersUrl = getProjectMembersUrl;
    }

    @Value("${group.members.api.url}")
    public void setGetGroupMembersUrl(String getGroupMembersUrl) {
        GitLabApiUtils.getGroupMembersUrl = getGroupMembersUrl;
    }

    @Value("${gitlab.token}")
    public void setToken(String token) {
        GitLabApiUtils.token = token;
    }

    /**
     * 获取所以项目成员
     * @param projectId
     * @return
     */
    public static List<String> getAllProjectMembers(Integer projectId) {
        getProjectMembersUrl = getProjectMembersUrl.replace("$",""+projectId);
        getGroupMembersUrl = getGroupMembersUrl.replace("$","800");
        List<String> projectMasterMembers = getGitLabMasterMembers(getProjectMembersUrl);
        List<String> groupMasterMembers = getGitLabMasterMembers(getGroupMembersUrl);
        return Stream.of(projectMasterMembers,groupMasterMembers).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    /**
     * 获取master成员
     * @param url
     * @return
     */
    private static List<String> getGitLabMasterMembers(String url) {
        List<String> result = new ArrayList<>();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).header("PRIVATE-TOKEN",token).build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            String body = response.body().string();
            JSONArray jsonArray = JSONArray.parseArray(body);
            for (int i = 0;i < jsonArray.size();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getInteger("access_level") == 40) {
                    result.add(jsonObject.getString("name"));
                }
            }
        } catch (IOException e) {
            log.error("调用GitLab API失败！",e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }
}
