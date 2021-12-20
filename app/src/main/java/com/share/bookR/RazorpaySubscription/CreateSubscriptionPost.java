package com.share.bookR.RazorpaySubscription;

public class CreateSubscriptionPost {

    private String planId;
    private Integer totalCount;
    /*private Integer quantity;
    private Integer customerNotify;
    private long startAt;
    private Integer expireBy;*/

    public CreateSubscriptionPost(String planId, Integer totalCount) {
        this.planId = planId;
        this.totalCount = totalCount;
    }

    public String getPlanId() {
        return planId;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}

