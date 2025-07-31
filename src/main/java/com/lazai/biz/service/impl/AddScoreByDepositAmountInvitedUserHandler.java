package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.ActionHandler;
import com.lazai.biz.service.ScoreBalanceService;
import com.lazai.core.common.RoleDTO;
import com.lazai.repostories.UserRepository;
import com.lazai.request.ScoreAddRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class AddScoreByDepositAmountInvitedUserHandler implements ActionHandler {

    @Autowired
    private ScoreBalanceService scoreBalanceService;

    @Autowired
    private UserRepository userRepository;

    /**
     * add score from context
     * @see ActionHandler#handle(JSONObject)
     */
    @Override
    public JSONObject handle(JSONObject context) {
        JSONObject extraParams = context.getJSONObject("extraParams");
        if(extraParams != null){
            JSONObject investment = extraParams.getJSONObject("investment");
            if(investment != null){
                String userId = context.getString("innerUserId");
                String scoreType = context.getString("scoreType");
                int score = calculateScore(investment.getBigInteger("balance"));
                String taskNo = context.getString("taskNo");
                ScoreAddRequest scoreAddRequest = new ScoreAddRequest();
                scoreAddRequest.setAppToken(context.getString("appToken"));
                scoreAddRequest.setScoreType(scoreType);
                scoreAddRequest.setUserId(userId);
                scoreAddRequest.setScore(BigInteger.valueOf(score));
                scoreAddRequest.setBizType("task");
                scoreAddRequest.setBizCode(taskNo);
                scoreAddRequest.setDirection("ADD");
                scoreAddRequest.setOperator(RoleDTO.getSystemOperator());
                scoreBalanceService.addUserScore(scoreAddRequest);
            }
        }
        return null;
    }

    private int calculateScore(BigInteger depositBalance){
        int rt = 0;
        BigInteger level0 = BigInteger.TEN.pow(18).multiply(BigInteger.valueOf(5));
        BigInteger level1 = BigInteger.TEN.pow(18).multiply(BigInteger.valueOf(20));
        BigInteger level2 = BigInteger.TEN.pow(18).multiply(BigInteger.valueOf(50));
        if(depositBalance.compareTo(level0) <= 0){
            return 500;
        }else if(depositBalance.compareTo(level0) > 0 && depositBalance.compareTo(level1) <= 0){
            return 1000;
        }else if(depositBalance.compareTo(level1) > 0 && depositBalance.compareTo(level2) <= 0){
            return 1500;
        }else{
            return 2000;
        }
    }

}
