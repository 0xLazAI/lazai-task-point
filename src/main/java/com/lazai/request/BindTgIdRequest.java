package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class BindTgIdRequest extends BasicRequest {
    private static final long serialVersionUID = -4809668630328024075L;

    private String userId;

    private String tgId;

    private String tgUserInfoStr;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTgId() {
        return tgId;
    }

    public void setTgId(String tgId) {
        this.tgId = tgId;
    }

    public String getTgUserInfoStr() {
        return tgUserInfoStr;
    }

    public void setTgUserInfoStr(String tgUserInfoStr) {
        this.tgUserInfoStr = tgUserInfoStr;
    }
}
