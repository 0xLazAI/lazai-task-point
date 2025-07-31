package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.entity.User;

public interface TaskVerifyHandler {

    public void verifyTaskDone(User user, JSONObject templateContext);

}
