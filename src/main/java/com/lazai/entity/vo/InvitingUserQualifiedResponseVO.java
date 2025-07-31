package com.lazai.entity.vo;

import java.io.Serializable;
import java.math.BigInteger;

public class InvitingUserQualifiedResponseVO implements Serializable {


    private static final long serialVersionUID = -8066649860454601511L;


    private Integer totalCount;

    private Integer qualifiedCount;

    private BigInteger totalScore;

    public BigInteger getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigInteger totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getQualifiedCount() {
        return qualifiedCount;
    }

    public void setQualifiedCount(Integer qualifiedCount) {
        this.qualifiedCount = qualifiedCount;
    }
}
