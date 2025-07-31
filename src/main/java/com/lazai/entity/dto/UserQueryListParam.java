package com.lazai.entity.dto;

import java.math.BigInteger;
import java.util.List;

public class UserQueryListParam {

    private List<BigInteger> userIds;

    public List<BigInteger> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<BigInteger> userIds) {
        this.userIds = userIds;
    }
}
