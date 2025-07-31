package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.LazbubuWhitelistSchedulerService;
import com.lazai.biz.service.TaskService;
import com.lazai.core.common.RoleDTO;
import com.lazai.entity.LazbubuDataWhitelist;
import com.lazai.entity.TaskRecord;
import com.lazai.entity.TaskTemplate;
import com.lazai.entity.User;
import com.lazai.entity.dto.LazbubuWhitelistQueryParam;
import com.lazai.entity.dto.TaskRecordQueryParam;
import com.lazai.enums.TaskStatusEnum;
import com.lazai.repostories.LazbubuWhitelistRepository;
import com.lazai.repostories.TaskRecordRepository;
import com.lazai.repostories.TaskTemplateRepository;
import com.lazai.repostories.UserRepository;
import com.lazai.request.TaskCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LazbubuWhitelistSchedulerServiceImpl implements LazbubuWhitelistSchedulerService {

    @Autowired
    private LazbubuWhitelistRepository lazbubuWhitelistRepository;

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");
    private static final Logger RESULT_LOGGER = LoggerFactory.getLogger("RESULT_LOG");

    @Scheduled(fixedRate = 120000) // 每2分钟检查一次
    public void checkLazbubuWhitelist(){
        RESULT_LOGGER.info("start scheduled white list check");
        LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
        List<String> statusList = new ArrayList<>();
        statusList.add("WAIT");
        statusList.add("ERROR");
        lazbubuWhitelistQueryParam.setStatusList(statusList);
        BigInteger minId = BigInteger.ZERO;
        lazbubuWhitelistQueryParam.setLimit(300);
        lazbubuWhitelistQueryParam.setMinId(minId);
        List<LazbubuDataWhitelist> whitelists = new ArrayList<>();
        TaskTemplate taskTemplate = taskTemplateRepository.selectByCode("invitedFriendsComplete");
        JSONObject taskTemplateContent = JSON.parseObject(taskTemplate.getContent());
        JSONObject context = taskTemplateContent.getJSONObject("context");
        do{
            whitelists = lazbubuWhitelistRepository.queryList(lazbubuWhitelistQueryParam);
            for(LazbubuDataWhitelist whitelist:whitelists){
                allDependencyTasksDone(context, whitelist);
                minId = whitelist.getId();
            }
            lazbubuWhitelistQueryParam.setMinId(minId);
        }while(!CollectionUtils.isEmpty(whitelists));
    }

    public void allDependencyTasksDone(JSONObject context, LazbubuDataWhitelist whitelist){
        TaskRecordQueryParam taskRecordQueryParam = new TaskRecordQueryParam();
        List<String> taskTemplateIds = context.getJSONArray("dependencyTasks");
        taskRecordQueryParam.setTaskTemplateIds(taskTemplateIds);
        taskRecordQueryParam.setInnerUser(whitelist.getUserId());
        taskRecordQueryParam.setStatusList(Collections.singletonList(TaskStatusEnum.FINISH.value()));

        List<TaskRecord> taskRecords = taskRecordRepository.queryList(taskRecordQueryParam);
        if(taskRecords.size() == taskTemplateIds.size()){
            User userInfo = userRepository.findById(whitelist.getUserId(), false);
            TaskCreateRequest taskCreateRequest = new TaskCreateRequest();
            taskCreateRequest.setInnerUserId(whitelist.getUserId());
            JSONObject whitelistContent = JSON.parseObject(whitelist.getContent());
            taskCreateRequest.setApp(whitelistContent.getJSONObject("taskContext").getString("appToken"));
            taskCreateRequest.setTemplateCode("joinLazbubuWhitelist");
            RoleDTO operator = new RoleDTO();
            operator.setId(whitelist.getUserId());
            operator.setName(userInfo.getName());
            taskCreateRequest.setOperator(operator);
            try{
                taskService.claimTask(taskCreateRequest);
            }catch (Throwable t){
                ERROR_LOGGER.error("lazbubu white list scheduler claim task error", t);
                whitelist.setStatus("ERROR");
                lazbubuWhitelistRepository.updateById(whitelist);
            }
        }
    }
}
