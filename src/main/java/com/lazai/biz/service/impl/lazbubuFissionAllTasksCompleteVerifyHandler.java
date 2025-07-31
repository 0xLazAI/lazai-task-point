package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TaskVerifyHandler;
import com.lazai.entity.TaskRecord;
import com.lazai.entity.User;
import com.lazai.entity.dto.TaskRecordQueryParam;
import com.lazai.exception.DomainException;
import com.lazai.repostories.TaskRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class lazbubuFissionAllTasksCompleteVerifyHandler implements TaskVerifyHandler {

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Override
    public void verifyTaskDone(User user, JSONObject templateContext) {
        TaskRecordQueryParam taskRecordQueryParam = new TaskRecordQueryParam();
        List<String> taskTemplateIds = templateContext.getJSONArray("dependencyTasks");

        taskRecordQueryParam.setTaskTemplateIds(taskTemplateIds);
        taskRecordQueryParam.setInnerUser(user.getId() + "");
        List<TaskRecord> taskRecords = taskRecordRepository.queryList(taskRecordQueryParam);
        if(taskRecords.size() != taskTemplateIds.size()){
            throw new DomainException("dependency tasks not finished!", 403);
        }
    }

}

