package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class FriendsTaskStatusPageQueryRequest extends BasicRequest {

    private static final long serialVersionUID = 1524527305543800050L;

    private String userId;

    private Integer page;

    private Integer pageSize;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
