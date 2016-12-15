package com.org.temp;

import java.util.Map;

import com.org.common.CommonConstant;

import net.sf.json.JSONArray;

/**
 * 三等奖
 */
public class AwardThree extends ParentAward implements BuMessageHander {
    private Map<String, String> paramsMap = null;

    public AwardThree() {
    }

    public AwardThree(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public JSONArray getMessage() {
        String level = this.paramsMap.get("level");

        // 为补抽奖做的兼容。
        String buCounts = this.paramsMap.get("buCounts");
        int awardCount = Integer.valueOf(buCounts);
        return bucj(CommonConstant.THIRD_USERLIST, level, awardCount);
    }
}
