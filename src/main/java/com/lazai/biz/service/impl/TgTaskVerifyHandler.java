package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TaskVerifyHandler;
import com.lazai.biz.service.TgSingleService;
import com.lazai.entity.User;
import com.lazai.exception.DomainException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TgTaskVerifyHandler implements TaskVerifyHandler {
    @Autowired
    private TgSingleService tgSingleService;

    @Override
    public void verifyTaskDone(User user, JSONObject templateContext) {
        if(StringUtils.isBlank(user.getTgId())){
            throw new DomainException("not bind tg account yet!",403);
        }
        Boolean isJoin = false;
        isJoin = tgSingleService.ifUserInGroup(user.getTgId(), templateContext.getString("botToken"), templateContext.getString("groupId"));
        if(!isJoin){
            throw new DomainException("not join tg group!",403);
        }
    }
}
