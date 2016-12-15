package com.org.temp;

import java.util.Map;

import com.org.common.CommonConstant;

import net.sf.json.JSONArray;

/**
 * 特等奖
 */
public class AwardSuper extends ParentAward implements BuMessageHander {
    private Map<String, String> paramsMap = null;

    public AwardSuper() {
    }

    public AwardSuper(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public JSONArray getMessage() {
        String level = this.paramsMap.get("level");

        // 为补抽奖做的兼容。
        String buCounts = this.paramsMap.get("buCounts");
        int awardCount = Integer.valueOf(buCounts);
        return bucj(CommonConstant.SUPER_USERLIST, level, awardCount);
    }
}
