
package com.alibaba.csp.sentinel.slots.block.flow.param;

import com.alibaba.csp.sentinel.config.SpringContextsUtil;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.model.TRiskInfoModel;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/** 描述: 自定义限流规则，与业务结合·
 * 从redis中实时读取规则
 * @author: JacX.
 * @date 2021/6/3 16:52
 * @see ParamFlowSlot
 */
public class RedisParamFlowSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    private RedisTemplate redisTemplate = (RedisTemplate) SpringContextsUtil.getBean(RedisTemplate.class);

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

    static List<ParamFlowRule> applyNonParamToParamRule(List<TRiskInfoModel> riskInfoModels, String resourceName) {
        List<ParamFlowRule> flowRules = new ArrayList<>();
        riskInfoModels.forEach(riskInfoModel -> {
            flowRules.add(applyNonParamToParamRule(riskInfoModel, resourceName, 0));
        });
        return flowRules;
    }

    private List<TRiskInfoModel> getFlowRuleByResourceName(String resourceName) {
        List<TRiskInfoModel> riskInfoModels = new ArrayList<>();
        String qpsFoundKey = resourceName;
        //仅存QPS
        Boolean hasQPSelfFlowRule = redisTemplate.opsForHash().hasKey("RISK_INFO_DEMESION:", qpsFoundKey);
        if (!hasQPSelfFlowRule) {
            qpsFoundKey = "DEFAULT|TYPE_SENTINEL_QPS";
        }
        TRiskInfoModel qpsRiskInfoModel = JSONObject.parseObject(redisTemplate.opsForHash().get("RISK_INFO_DEMESION:", qpsFoundKey) + "", TRiskInfoModel.class);

        String dayFoundKey = qpsFoundKey.substring(0, qpsFoundKey.indexOf("|") + 1) + "TYPE_SENTINEL_DAY";
        Boolean hasDaySelfFlowRule = redisTemplate.opsForHash().hasKey("RISK_INFO_DEMESION:", qpsFoundKey);
        if (!hasDaySelfFlowRule) {
            dayFoundKey = "DEFAULT|TYPE_SENTINEL_DAY";
        }
        TRiskInfoModel dayRiskInfoModel = JSONObject.parseObject(redisTemplate.opsForHash().get("RISK_INFO_DEMESION:", dayFoundKey) + "", TRiskInfoModel.class);

        String monthFoundKey = qpsFoundKey.substring(0, qpsFoundKey.indexOf("|") + 1) + "TYPE_SENTINEL_MONTH";
        Boolean hasMonthSelfFlowRule = redisTemplate.opsForHash().hasKey("RISK_INFO_DEMESION:", monthFoundKey);
        if (!hasMonthSelfFlowRule) {
            monthFoundKey = "DEFAULT|TYPE_SENTINEL_MONTH";
        }
        TRiskInfoModel monthRiskInfoModel = JSONObject.parseObject(redisTemplate.opsForHash().get("RISK_INFO_DEMESION:", monthFoundKey) + "", TRiskInfoModel.class);

        riskInfoModels.add(qpsRiskInfoModel);
        riskInfoModels.add(dayRiskInfoModel);
        riskInfoModels.add(monthRiskInfoModel);
        return riskInfoModels;
    }
}
