package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TaskVerifyHandler;
import com.lazai.entity.TaskRecord;
import com.lazai.entity.User;
import com.lazai.entity.UserInvites;
import com.lazai.entity.dto.TaskRecordQueryParam;
import com.lazai.exception.DomainException;
import com.lazai.repostories.TaskRecordRepository;
import com.lazai.repostories.UserInvitesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvitedFriendsCompleteVerifyHandler implements TaskVerifyHandler {
    @Autowired
    private UserInvitesRepository userInvitesRepository;

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Override
    public void verifyTaskDone(User user, JSONObject templateContext) {
        List<UserInvites> userInvites = userInvitesRepository.getByInvitingUserId(user.getId() + "");
        Integer userInvitesCntTarget = templateContext.getInteger("needInvitedUser");
        if(userInvites.size() < userInvitesCntTarget){
            throw new DomainException("The target number of invited users is " + userInvitesCntTarget + ", now you have invited " + userInvites.size(), 403);
        }
        TaskRecordQueryParam taskRecordQueryParam = new TaskRecordQueryParam();
        taskRecordQueryParam.setInnerUsers(userInvites.stream().map(UserInvites::getInvitedUser).collect(Collectors.toList()));
        List<String> taskTemplateIds = templateContext.getJSONArray("dependencyTasks");
        taskTemplateIds = taskTemplateIds.stream().filter(a->!a.equals("tgGroupJoin")).toList();
        taskRecordQueryParam.setTaskTemplateIds(taskTemplateIds);
        List<TaskRecord> invitingUserTasks = taskRecordRepository.queryList(taskRecordQueryParam);
        int finishCnt = 0;
        Map<String, Boolean> hasCnt = new HashMap<>();
        Map<String, Integer> userFinishMap = new HashMap<>();
        for(TaskRecord taskRecord:invitingUserTasks){
            if(!userFinishMap.containsKey(taskRecord.getInnerUser())){
                userFinishMap.put(taskRecord.getInnerUser(), 0);
            }
            userFinishMap.compute(taskRecord.getInnerUser(), (k, cnt) -> cnt + 1);
            if(userFinishMap.get(taskRecord.getInnerUser()) >= taskTemplateIds.size()){
                if(!hasCnt.containsKey(taskRecord.getInnerUser())){
                    finishCnt++;
                    hasCnt.put(taskRecord.getInnerUser(), true);
                }
            }
        }
        if(finishCnt < templateContext.getInteger("needInvitedUser")){
            throw new DomainException("The number of users you invited to complete the task is less than " + templateContext.getInteger("needInvitedUser"), 403);
        }
    }
}
