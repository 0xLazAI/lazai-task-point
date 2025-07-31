package com.lazai.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lazai.core.common.BasicRequest;
import com.lazai.core.common.ToLowerCaseDeserializer;

public class BindEthAddressRequest extends BasicRequest {


    private static final long serialVersionUID = 310249798524537362L;

    private String userId;

    @JsonDeserialize(using = ToLowerCaseDeserializer.class)
    private String ethAddress;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }
}
