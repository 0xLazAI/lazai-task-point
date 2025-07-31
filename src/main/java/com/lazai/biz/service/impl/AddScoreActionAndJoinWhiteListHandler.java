package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.ActionHandler;
import com.lazai.biz.service.LazbubuWhitelistSchedulerService;
import com.lazai.biz.service.ScoreBalanceService;
import com.lazai.core.common.RoleDTO;
import com.lazai.entity.LazbubuDataWhitelist;
import com.lazai.entity.dto.LazbubuWhitelistQueryParam;
import com.lazai.repostories.LazbubuWhitelistRepository;
import com.lazai.repostories.TaskRecordRepository;
import com.lazai.request.ScoreAddRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.List;

@Service
public class AddScoreActionAndJoinWhiteListHandler implements ActionHandler {

    @Autowired
    private ScoreBalanceService scoreBalanceService;

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Autowired
    private LazbubuWhitelistRepository lazbubuWhitelistRepository;

    @Autowired
    private LazbubuWhitelistSchedulerService lazbubuWhitelistSchedulerService;

    @Override
    public JSONObject handle(JSONObject context) {
        String userId = context.getString("innerUserId");
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

        LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
        lazbubuWhitelistQueryParam.setUserId(userId);

        List<LazbubuDataWhitelist> whitelistExists = lazbubuWhitelistRepository.queryList(lazbubuWhitelistQueryParam);
        LazbubuDataWhitelist lazbubuDataWhitelist = null;
        if(CollectionUtils.isEmpty(whitelistExists)){
             lazbubuDataWhitelist = new LazbubuDataWhitelist();
            lazbubuDataWhitelist.setUserId(userId);
            lazbubuDataWhitelist.setBizType("lazbubuFission");
            JSONObject content = new JSONObject();
            content.put("taskContext", context);
            lazbubuDataWhitelist.setContent(content.toJSONString());
            lazbubuDataWhitelist.setSource(context.getString("appToken"));

            lazbubuWhitelistRepository.insert(lazbubuDataWhitelist);
        }else {
            lazbubuDataWhitelist = whitelistExists.get(0);
        }

        //check dependency tasks are done
        lazbubuWhitelistSchedulerService.allDependencyTasksDone(context, lazbubuDataWhitelist);
        return null;
    }

}
