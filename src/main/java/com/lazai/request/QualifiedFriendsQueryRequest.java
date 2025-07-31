package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class QualifiedFriendsQueryRequest extends BasicRequest {

    private static final long serialVersionUID = -7513477138294510301L;

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
