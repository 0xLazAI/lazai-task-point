package com.lazai.entity.dto;

import java.util.Date;
import java.util.List;

public class TaskRecordQueryParam {

    private List<String> statusList;

    private List<String> taskNos;

    private String taskTemplateId;

    private String creator;

    private String innerUser;

    private List<String> innerUsers;

    private String outerUser;

    private String app;

    private List<String> taskTemplateIds;

    private String bizId;

    private String bizType;

    private Date createdStart;

    private Date createdEnd;

    public List<String> getInnerUsers() {
        return innerUsers;
    }

    public void setInnerUsers(List<String> innerUsers) {
        this.innerUsers = innerUsers;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Date getCreatedStart() {
        return createdStart;
    }

    public void setCreatedStart(Date createdStart) {
        this.createdStart = createdStart;
    }

    public Date getCreatedEnd() {
        return createdEnd;
    }

    public void setCreatedEnd(Date createdEnd) {
        this.createdEnd = createdEnd;
    }

    public List<String> getTaskTemplateIds() {
        return taskTemplateIds;
    }

    public void setTaskTemplateIds(List<String> taskTemplateIds) {
        this.taskTemplateIds = taskTemplateIds;
    }

    public String getInnerUser() {
        return innerUser;
    }

    public void setInnerUser(String innerUser) {
        this.innerUser = innerUser;
    }

    public String getOuterUser() {
        return outerUser;
    }

    public void setOuterUser(String outerUser) {
        this.outerUser = outerUser;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public List<String> getTaskNos() {
        return taskNos;
    }

    public void setTaskNos(List<String> taskNos) {
        this.taskNos = taskNos;
    }

    public String getTaskTemplateId() {
        return taskTemplateId;
    }

    public void setTaskTemplateId(String taskTemplateId) {
        this.taskTemplateId = taskTemplateId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
