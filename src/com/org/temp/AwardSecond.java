package com.org.temp;

import java.util.Map;

import com.org.common.CommonConstant;

import net.sf.json.JSONArray;

/**
 * 二等奖
 */
public class AwardSecond extends ParentAward implements BuMessageHander {
    private Map<String, String> paramsMap = null;

    public AwardSecond() {
    }

    public AwardSecond(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public JSONArray getMessage() {
        String level = this.paramsMap.get("level");

        // 为补抽奖做的兼容。
        String buCounts = this.paramsMap.get("buCounts");
        int awardCount = Integer.valueOf(buCounts);
        return bucj(CommonConstant.SECOND_USERLIST, level, awardCount);
    }

}
