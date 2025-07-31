package com.lazai.entity;

import java.math.BigInteger;
import java.util.Date;

public class LazbubuDataWhitelist {

    private BigInteger id;

    private String userId;

    private String status;

    private String bizType;

    private String content;

    private String source;

    private Date createdAt;

    private Date updatedAt;

    private Integer completedCnt;

    public Integer getCompletedCnt() {
        return completedCnt;
    }

    public void setCompletedCnt(Integer completedCnt) {
        this.completedCnt = completedCnt;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
