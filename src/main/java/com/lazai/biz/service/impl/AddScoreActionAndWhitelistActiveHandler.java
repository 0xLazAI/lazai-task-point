package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.lazai.biz.service.ActionHandler;
import com.lazai.biz.service.ScoreBalanceService;
import com.lazai.biz.service.TaskService;
import com.lazai.core.common.RoleDTO;
import com.lazai.entity.LazbubuDataWhitelist;
import com.lazai.entity.User;
import com.lazai.entity.UserInvites;
import com.lazai.entity.dto.LazbubuWhitelistQueryParam;
import com.lazai.repostories.LazbubuWhitelistRepository;
import com.lazai.repostories.TaskRecordRepository;
import com.lazai.repostories.UserInvitesRepository;
import com.lazai.repostories.UserRepository;
import com.lazai.request.ScoreAddRequest;
import com.lazai.request.TaskCreateRequest;
import com.lazai.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.List;

@Service
public class AddScoreActionAndWhitelistActiveHandler implements ActionHandler {

    @Autowired
    private ScoreBalanceService scoreBalanceService;

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Autowired
    private LazbubuWhitelistRepository lazbubuWhitelistRepository;

    @Autowired
    private UserInvitesRepository userInvitesRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    public static final String WHITE_LIST_KEY = "lazbubu:whitelist";

    @Override
    public JSONObject handle(JSONObject context) {
        String userId = context.getString("innerUserId");
        LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
        lazbubuWhitelistQueryParam.setBizType("lazbubuFission");
        lazbubuWhitelistQueryParam.setUserId(userId);

        List<LazbubuDataWhitelist> lazbubuDataWhitelistList = lazbubuWhitelistRepository.queryList(lazbubuWhitelistQueryParam);

        if(!CollectionUtils.isEmpty(lazbubuDataWhitelistList) && !lazbubuDataWhitelistList.get(0).getStatus().equals("ACTIVE")){
            String scoreType = context.getString("scoreType");
            BigInteger score = context.getBigInteger("score");
            String taskNo = context.getString("taskNo");
            ScoreAddRequest scoreAddRequest = new ScoreAddRequest();
            scoreAddRequest.setAppToken(context.getString("appToken"));
            scoreAddRequest.setScoreType(scoreType);
            scoreAddRequest.setUserId(userId);
            scoreAddRequest.setScore(score);
            scoreAddRequest.setBizType("task");
            scoreAddRequest.setBizCode(taskNo);
            scoreAddRequest.setDirection("ADD");
            scoreAddRequest.setOperator(RoleDTO.getSystemOperator());
            scoreBalanceService.addUserScore(scoreAddRequest);
            LazbubuDataWhitelist whitelistRecord = lazbubuDataWhitelistList.get(0);
            whitelistRecord.setStatus("ACTIVE");
            lazbubuWhitelistRepository.updateById(whitelistRecord);
        }

        List<UserInvites> userInvites = userInvitesRepository.getByInvitedUserId(userId);
        if(!CollectionUtils.isEmpty(userInvites)){
            UserInvites userInvite = userInvites.get(0);
            User invitedUserInfo = userRepository.findById(userInvite.getInvitingUser(), false);
            TaskCreateRequest taskCreateRequest = new TaskCreateRequest();
            taskCreateRequest.setInnerUserId(invitedUserInfo.getId() + "");
            taskCreateRequest.setApp(context.getString("appToken"));
            taskCreateRequest.setTemplateCode("invitedUserReward");
            RoleDTO operator = new RoleDTO();
            operator.setId(invitedUserInfo.getId() + "");
            operator.setName(invitedUserInfo.getName());
            taskCreateRequest.setOperator(operator);
            taskService.claimTask(taskCreateRequest);

            LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam2 = new LazbubuWhitelistQueryParam();
            lazbubuWhitelistQueryParam2.setUserId(userInvite.getInvitingUser());
            List<LazbubuDataWhitelist> invitedUserWhitelistInfo = lazbubuWhitelistRepository.queryList(lazbubuWhitelistQueryParam2);
            if(!CollectionUtils.isEmpty(invitedUserWhitelistInfo)){
                LazbubuDataWhitelist invitedUserWhitelistInfoSingle = invitedUserWhitelistInfo.get(0);
                invitedUserWhitelistInfoSingle.setCompletedCnt(invitedUserWhitelistInfoSingle.getCompletedCnt() + 1);
                lazbubuWhitelistRepository.updateById(invitedUserWhitelistInfoSingle);
                User userInfo = userRepository.findById(invitedUserWhitelistInfoSingle.getUserId(), false);
                JSONObject userSimple = new JSONObject();
                userSimple.put("id", userInfo.getId());
                userSimple.put("name", userInfo.getName());
                userSimple.put("ethAddress", userInfo.getEthAddress());
                RedisUtils.ZINCRBY(WHITE_LIST_KEY, JSON.toJSONString(userSimple), 1);
            }
        }

        return null;
    }


}
