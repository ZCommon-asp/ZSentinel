package com.alibaba.csp.sentinel.slots.block.flow.param;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.model.TRiskInfoModel;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.statistic.data.FlowRiskDefinition;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: JacX.
 * @description:从缓存中读取规则
 * @Date: 2021/6/4 10:34
 * @Modified By:JacX.
 * @see
 * @since
 */
public class CacheParamFlowSlot extends AbstractLinkedProcessorSlot<DefaultNode> {
   private static final Map<String,TRiskInfoModel> tRiskInfoModelMap =  FlowRiskDefinition.getRisk();


    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count,
                      boolean prioritized, Object... args) throws Throwable {
        checkFlow(resourceWrapper, count, args);
        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }

    void applyRealParamIdx(/*@NonNull*/ ParamFlowRule rule, int length) {
        int paramIdx = rule.getParamIdx();
        if (paramIdx < 0) {
            if (-paramIdx <= length) {
                rule.setParamIdx(length + paramIdx);
            } else {
                // Illegal index, give it a illegal positive value, latter rule checking will pass.
                rule.setParamIdx(-paramIdx);
            }
        }
    }

    void checkFlow(ResourceWrapper resourceWrapper, int count, Object... args) throws BlockException {
        if (args == null) {
            return;
        }
        String resourceName = resourceWrapper.getName();
        List<ParamFlowRule> rules = applyNonParamToParamRule(getFlowRuleByResourceName(resourceName), resourceName);
        for (ParamFlowRule rule : rules) {
            applyRealParamIdx(rule, args.length);

            // Initialize the parameter metrics.
            ParameterMetricStorage.initParamMetricsFor(resourceWrapper, rule);

            if (!ParamFlowChecker.passCheck(resourceWrapper, rule, count, args)) {
                String triggeredParam = "";
                if (args.length > rule.getParamIdx()) {
                    Object value = args[rule.getParamIdx()];
                    triggeredParam = String.valueOf(value);
                }
                throw new ParamFlowException(resourceName, triggeredParam, rule);
            }
        }
    }


    static ParamFlowRule applyNonParamToParamRule(/*@Valid*/ TRiskInfoModel riskInfoModel, String resourceName, int idx) {
        return new ParamFlowRule(resourceName)
                .setCount(riskInfoModel.getLimitCount())
                .setGrade(1)
                .setDurationInSec(riskInfoModel.getQuotaPeriod())
                .setBurstCount(0)
                .setControlBehavior(0)
                .setMaxQueueingTimeMs(500)
                .setParamIdx(idx);
    }

    public static List<ParamFlowRule> applyNonParamToParamRule(List<TRiskInfoModel> riskInfoModels, String resourceName) {
        List<ParamFlowRule> flowRules = new ArrayList<>();
        riskInfoModels.forEach(riskInfoModel -> {
            flowRules.add(applyNonParamToParamRule(riskInfoModel, resourceName, 0));
        });
        return flowRules;
    }

    public static List<TRiskInfoModel> getFlowRuleByResourceName(String resourceName) {
        List<TRiskInfoModel> riskInfoModels = new ArrayList<>();
        TRiskInfoModel riskInfoModel = tRiskInfoModelMap.get(resourceName);
        //仅存QPS
        if (null == riskInfoModel) {
            if (resourceName.contains("TYPE_SENTINEL_DAY")) {
                resourceName = "DEFAULT|TYPE_SENTINEL_DAY";
            } else if (resourceName.contains("TYPE_SENTINEL_MONTH")) {
                resourceName = "DEFAULT|TYPE_SENTINEL_MONTH";
            } else {
                resourceName = "DEFAULT|TYPE_SENTINEL_QPS";
            }
        }
        riskInfoModel = tRiskInfoModelMap.get(resourceName);

        if (null != riskInfoModel) {
            riskInfoModels.add(riskInfoModel);
        }
        return riskInfoModels;
    }
}
