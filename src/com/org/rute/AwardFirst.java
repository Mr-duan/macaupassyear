package com.org.rute;

import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Component;

import com.org.common.CommonConstant;
import com.org.utils.PropertiesUtil;

/**
 * һ�Ƚ�
 */
@Component
public class AwardFirst extends ParentAward implements MessageHander{
	private Map<String,String> paramsMap = null;
	public AwardFirst(){}
	public AwardFirst(Map<String,String> paramsMap){
		this.paramsMap = paramsMap;
	}
	
	@Override
	public JSONArray getMessage() {
		String level = paramsMap.get("level");
		int awardCount = Integer.valueOf(PropertiesUtil.getValue("award", level));
		return init(CommonConstant.FIRST_USERLIST, level, awardCount);
	}
}
