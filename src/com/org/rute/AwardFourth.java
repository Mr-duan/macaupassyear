package com.org.rute;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.org.common.CommonConstant;
import com.org.utils.PropertiesUtil;

import net.sf.json.JSONArray;

/**
 * 四等奖
 */
@Component
public class AwardFourth extends ParentAward implements MessageHander {
    private Map<String, String> paramsMap = null;

    public AwardFourth() {
    }

    public AwardFourth(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public JSONArray getMessage() {
        String level = this.paramsMap.get("level");
        int awardCount = Integer.valueOf(PropertiesUtil.getValue("award", level));
        return init(CommonConstant.FOURTH_USERLIST, level, awardCount);
    }
}
