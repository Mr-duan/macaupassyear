package com.org.rute;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.org.common.CommonConstant;
import com.org.utils.PropertiesUtil;

import net.sf.json.JSONArray;

/**
 * 特等奖
 */
@Component
public class AwardSuper extends ParentAward implements MessageHander {
    private Map<String, String> paramsMap = null;

    public AwardSuper() {
    }

    public AwardSuper(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public JSONArray getMessage() {
        String level = this.paramsMap.get("level");
        int awardCount = Integer.valueOf(PropertiesUtil.getValue("award", level));
        return initTemporary(CommonConstant.SUPER_USERLIST, level, awardCount);
    }
}
