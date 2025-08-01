package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.WhitelistAdminBizService;
import com.lazai.entity.dto.LazbubuWhitelistQueryParam;
import com.lazai.entity.dto.TaskRecordQueryParam;
import com.lazai.enums.TaskStatusEnum;
import com.lazai.repostories.LazbubuWhitelistRepository;
import com.lazai.repostories.TaskRecordRepository;
import com.lazai.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Service
public class WhitelistAdminBizServiceImpl implements WhitelistAdminBizService {

    @Autowired
    private LazbubuWhitelistRepository lazbubuWhitelistRepository;

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    public JSONObject totalData(){
        LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
        //lazbubuWhitelistQueryParam.setMinId(BigInteger.valueOf(24));
        lazbubuWhitelistQueryParam.setStatusList(Collections.singletonList("ACTIVE"));
        Integer whitelistTotalCnt = lazbubuWhitelistRepository.pageQueryCnt(lazbubuWhitelistQueryParam);

        TaskRecordQueryParam lazaiTwitterParam = new TaskRecordQueryParam();
        lazaiTwitterParam.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
        lazaiTwitterParam.setTaskTemplateId("twitterFocusLazAI");
        Integer lazaiTwitterCnt = taskRecordRepository.queryListCnt(lazaiTwitterParam);

        TaskRecordQueryParam lazpadTwitterParam = new TaskRecordQueryParam();
        lazpadTwitterParam.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
        lazpadTwitterParam.setTaskTemplateId("twitterFocusLazpad");
        Integer lazpadTwitterCnt = taskRecordRepository.queryListCnt(lazpadTwitterParam);

        TaskRecordQueryParam tgJoinParam = new TaskRecordQueryParam();
        tgJoinParam.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
        tgJoinParam.setTaskTemplateId("tgGroupJoin");
        Integer tgJoinCnt = taskRecordRepository.queryListCnt(tgJoinParam);

        TaskRecordQueryParam inviteFriendFinishParam = new TaskRecordQueryParam();
        inviteFriendFinishParam.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
        inviteFriendFinishParam.setTaskTemplateId("invitedFriendsComplete");
        Integer inviteFinishCnt = taskRecordRepository.queryListCnt(inviteFriendFinishParam);

        JSONObject result = new JSONObject();
        result.put("whitelistTotalCount", whitelistTotalCnt);
        result.put("lazaiTwitterFocusTotalCount", lazaiTwitterCnt);
        result.put("lazpadTwitterFocusTotalCount", lazpadTwitterCnt);
        result.put("tgGroupJoinInTotalCount", tgJoinCnt);
        result.put("inviteFriendsFinishTotalCount", inviteFinishCnt);
        return result;
    }

    public JSONObject getDailyData(Date startDate, Date endDate){
        LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
        lazbubuWhitelistQueryParam.setCreatedStart(startDate);
        lazbubuWhitelistQueryParam.setCreatedEnd(endDate);
        lazbubuWhitelistQueryParam.setStatusList(Collections.singletonList("ACTIVE"));
        Integer whitelistTotalCnt = lazbubuWhitelistRepository.pageQueryCnt(lazbubuWhitelistQueryParam);

        TaskRecordQueryParam lazaiTwitterParam = new TaskRecordQueryParam();
        lazaiTwitterParam.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
        lazaiTwitterParam.setTaskTemplateId("twitterFocusLazAI");
        lazaiTwitterParam.setCreatedStart(startDate);
        lazaiTwitterParam.setCreatedEnd(endDate);
        Integer lazaiTwitterCnt = taskRecordRepository.queryListCnt(lazaiTwitterParam);

        TaskRecordQueryParam lazpadTwitterParam = new TaskRecordQueryParam();
        lazpadTwitterParam.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
        lazpadTwitterParam.setTaskTemplateId("twitterFocusLazpad");
        lazpadTwitterParam.setCreatedStart(startDate);
        lazpadTwitterParam.setCreatedEnd(endDate);
        Integer lazpadTwitterCnt = taskRecordRepository.queryListCnt(lazpadTwitterParam);

        TaskRecordQueryParam tgJoinParam = new TaskRecordQueryParam();
        tgJoinParam.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
        tgJoinParam.setTaskTemplateId("tgGroupJoin");
        tgJoinParam.setCreatedStart(startDate);
        tgJoinParam.setCreatedEnd(endDate);
        Integer tgJoinCnt = taskRecordRepository.queryListCnt(tgJoinParam);

        TaskRecordQueryParam inviteFriendFinishParam = new TaskRecordQueryParam();
        inviteFriendFinishParam.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
        inviteFriendFinishParam.setTaskTemplateId("invitedFriendsComplete");
        inviteFriendFinishParam.setCreatedStart(startDate);
        inviteFriendFinishParam.setCreatedEnd(endDate);
        Integer inviteFinishCnt = taskRecordRepository.queryListCnt(inviteFriendFinishParam);

        JSONObject result = new JSONObject();
        result.put("whitelistCount", whitelistTotalCnt);
        result.put("lazaiTwitterFocusCount", lazaiTwitterCnt);
        result.put("lazpadTwitterFocusCount", lazpadTwitterCnt);
        result.put("tgGroupJoinInCount", tgJoinCnt);
        result.put("inviteFriendsFinishCount", inviteFinishCnt);
        return result;
    }

    public JSONObject getAll(){
        JSONObject all = new JSONObject();
        all.put("totalData", totalData());
        JSONObject daysData = new JSONObject();
        for(int i = 0; i <= 5; i++){
            Date start = DateUtils.getSubDaysStartOfDay(i);
            Date end = DateUtils.getSubDaysEndOfDay(i);
            String dateFormat = DateUtils.dateTime(start);
            daysData.put(dateFormat, getDailyData(start, end));
        }
        all.put("daily", daysData);
        return all;
    }
}
