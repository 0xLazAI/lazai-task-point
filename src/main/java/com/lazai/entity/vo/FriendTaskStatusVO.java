package com.lazai.entity.vo;

import java.io.Serializable;

public class FriendTaskStatusVO implements Serializable {

    private static final long serialVersionUID = 5012224619596628358L;

    private String address;

    private String status;

    private Boolean inWhiteList;

    public Boolean getInWhiteList() {
        return inWhiteList;
    }

    public void setInWhiteList(Boolean inWhiteList) {
        this.inWhiteList = inWhiteList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
