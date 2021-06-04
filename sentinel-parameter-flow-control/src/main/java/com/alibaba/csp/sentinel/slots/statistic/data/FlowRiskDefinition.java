package com.alibaba.csp.sentinel.slots.statistic.data;

import com.alibaba.csp.sentinel.model.TRiskInfoModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: JacX.
 * @description:规则类。此处没有使用paramFlowRule，避免规则过多
 * @Date: 2021/6/4 10:36
 * @Modified By:JacX.
 * @see
 * @since
 */
public class FlowRiskDefinition {
    public static Map<String, TRiskInfoModel> riskDefinition = new ConcurrentHashMap();

    public static synchronized void convertRisk(List<TRiskInfoModel> riskInfoModels) {
        if (null == riskInfoModels || riskInfoModels.isEmpty()) {
            return;
        }
        riskInfoModels.forEach(riskInfoModel -> convertRisk(riskInfoModel));
    }

    public static synchronized void convertRisk(TRiskInfoModel riskInfoModel) {
        riskDefinition.put(riskInfoModel.getRiskDemension(), riskInfoModel);
    }

    public static synchronized Map<String, TRiskInfoModel> getRisk() {
        return riskDefinition;
    }

}
