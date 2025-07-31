package com.lazai.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lazai.core.common.BasicRequest;
import com.lazai.core.common.ToLowerCaseDeserializer;

public class BindUserEthRequest extends BasicRequest {


    private static final long serialVersionUID = -888468357186834300L;

    @JsonDeserialize(using = ToLowerCaseDeserializer.class)
    private String ethAddress;

    private String userId;

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
