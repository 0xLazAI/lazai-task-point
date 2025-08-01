package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.entity.LazbubuDataWhitelist;

public interface LazbubuWhitelistSchedulerService {
    void checkLazbubuWhitelist();
    void syncFinishCnt();
    void allDependencyTasksDone(JSONObject context, LazbubuDataWhitelist whitelist);
}
