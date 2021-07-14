package com.alibaba.csp.sentinel.model;


import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */

public class TRiskInfoModel implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 风控维度 唯一键。风控维度-1。目前为null/appId/appId+“|” + 接口名称。为null时该条风控规则全局有效，后续需增加维度时，每个维度之间用 | 分隔。如果该条规则多个起作用，则维度之间用，号分隔。
     */
    private String riskDemension;

    /**
     * 风控限额 风控数量。目前此额度为基准额度，需要根据百分比触发不同动作
     */
    private Integer riskQuota;

    /**
     * 限额周期 当risk_type=TYPE_SENTINEL_QPS时单位秒。结合风控限额决定维度的限流触发机制。当risk_type=TYPE_ERROR时单位次，结合风控限额决定维度的限流触发机制，如25/1000次
     */
    private Integer quotaPeriod;

    /**
     * 生效类型 风控类型。TYPE_SENTINEL_QPS=启用sentinel限流；TYPE_ERROR=根据错误次数限流。当前sentinel的flowRule仅查询TYPE_SENTINEL_QPS
     */
    private String riskType;

    /**
     * 预警事件阈值 阈值100%≤每秒请求次数＜阈值120%
     */
    private Integer thresholdWarn;

    private Integer warnRemainCount;

    /**
     * 限流事件阈值 阈值120%≤每秒请求次数＜阈值150%
     */
    private Integer thresholdLimit;

    private Integer limitCount;

    /**
     * 暂停服务事件阈值 1、1分钟内限流超过5次（时间周期和次数作为配置项） 2、1000次请求失败率＞25%（请求次数和失败率作为配置项）
     */
    private String thresholdStop;

    /**
     * 熔断事件阈值 24小时内触发3次以上暂停服务
     */
    private String thresholdFusing;

    /**
     * 创建人 英文加密手机号
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新人 英文加密手机号
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private Date updatedTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRiskDemension() {
        return riskDemension;
    }

    public void setRiskDemension(String riskDemension) {
        this.riskDemension = riskDemension;
    }

    public Integer getRiskQuota() {
        return riskQuota;
    }

    public void setRiskQuota(Integer riskQuota) {
        this.riskQuota = riskQuota;
    }

    public Integer getQuotaPeriod() {
        return quotaPeriod;
    }

    public void setQuotaPeriod(Integer quotaPeriod) {
        this.quotaPeriod = quotaPeriod;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public Integer getThresholdWarn() {
        return thresholdWarn;
    }

    public void setThresholdWarn(Integer thresholdWarn) {
        this.thresholdWarn = thresholdWarn;
    }

    public void setWarnRemainCount(Integer warnRemainCount) {
        this.warnRemainCount = warnRemainCount;
    }

    public Integer getThresholdLimit() {
        return thresholdLimit;
    }

    public void setThresholdLimit(Integer thresholdLimit) {
        this.thresholdLimit = thresholdLimit;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public String getThresholdStop() {
        return thresholdStop;
    }

    public void setThresholdStop(String thresholdStop) {
        this.thresholdStop = thresholdStop;
    }

    public String getThresholdFusing() {
        return thresholdFusing;
    }

    public void setThresholdFusing(String thresholdFusing) {
        this.thresholdFusing = thresholdFusing;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getWarnRemainCount() {
        Integer thresholdWarn = getThresholdWarn();
        if (null == thresholdWarn) {
            return null;
        }
        return getLimitCount() - getRiskQuota() * thresholdWarn / 100;
    }

    public Integer getLimitCount() {
        return getRiskQuota() * getThresholdLimit() / 100;
    }
}