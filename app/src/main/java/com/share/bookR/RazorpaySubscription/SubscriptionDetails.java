package com.share.bookR.RazorpaySubscription;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscriptionDetails {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("entity")
    @Expose
    private String entity;
    @SerializedName("plan_id")
    @Expose
    private String planId;
    @SerializedName("customer_id")
    @Expose
    private Object customerId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("current_start")
    @Expose
    private Integer currentStart;
    @SerializedName("current_end")
    @Expose
    private Integer currentEnd;
    @SerializedName("ended_at")
    @Expose
    private Object endedAt;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("notes")
    @Expose
    private List<Object> notes = null;
    @SerializedName("charge_at")
    @Expose
    private Integer chargeAt;
    @SerializedName("start_at")
    @Expose
    private Integer startAt;
    @SerializedName("end_at")
    @Expose
    private Integer endAt;
    @SerializedName("auth_attempts")
    @Expose
    private Integer authAttempts;
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("paid_count")
    @Expose
    private Integer paidCount;
    @SerializedName("customer_notify")
    @Expose
    private Boolean customerNotify;
    @SerializedName("created_at")
    @Expose
    private Integer createdAt;
    @SerializedName("expire_by")
    @Expose
    private Object expireBy;
    @SerializedName("short_url")
    @Expose
    private String shortUrl;
    @SerializedName("has_scheduled_changes")
    @Expose
    private Boolean hasScheduledChanges;
    @SerializedName("change_scheduled_at")
    @Expose
    private Object changeScheduledAt;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("offer_id")
    @Expose
    private Object offerId;
    @SerializedName("remaining_count")
    @Expose
    private Integer remainingCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Object getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Object customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCurrentStart() {
        return currentStart;
    }

    public void setCurrentStart(Integer currentStart) {
        this.currentStart = currentStart;
    }

    public Integer getCurrentEnd() {
        return currentEnd;
    }

    public void setCurrentEnd(Integer currentEnd) {
        this.currentEnd = currentEnd;
    }

    public Object getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Object endedAt) {
        this.endedAt = endedAt;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<Object> getNotes() {
        return notes;
    }

    public void setNotes(List<Object> notes) {
        this.notes = notes;
    }

    public Integer getChargeAt() {
        return chargeAt;
    }

    public void setChargeAt(Integer chargeAt) {
        this.chargeAt = chargeAt;
    }

    public Integer getStartAt() {
        return startAt;
    }

    public void setStartAt(Integer startAt) {
        this.startAt = startAt;
    }

    public Integer getEndAt() {
        return endAt;
    }

    public void setEndAt(Integer endAt) {
        this.endAt = endAt;
    }

    public Integer getAuthAttempts() {
        return authAttempts;
    }

    public void setAuthAttempts(Integer authAttempts) {
        this.authAttempts = authAttempts;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPaidCount() {
        return paidCount;
    }

    public void setPaidCount(Integer paidCount) {
        this.paidCount = paidCount;
    }

    public Boolean getCustomerNotify() {
        return customerNotify;
    }

    public void setCustomerNotify(Boolean customerNotify) {
        this.customerNotify = customerNotify;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public Object getExpireBy() {
        return expireBy;
    }

    public void setExpireBy(Object expireBy) {
        this.expireBy = expireBy;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public Boolean getHasScheduledChanges() {
        return hasScheduledChanges;
    }

    public void setHasScheduledChanges(Boolean hasScheduledChanges) {
        this.hasScheduledChanges = hasScheduledChanges;
    }

    public Object getChangeScheduledAt() {
        return changeScheduledAt;
    }

    public void setChangeScheduledAt(Object changeScheduledAt) {
        this.changeScheduledAt = changeScheduledAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Object getOfferId() {
        return offerId;
    }

    public void setOfferId(Object offerId) {
        this.offerId = offerId;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }
}
