package com.org.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.org.common.CommonConstant;
import com.org.container.CommonContainer;
import com.org.dao.CommonDao;
import com.org.util.SpringUtil;

import net.sf.json.JSONArray;

public class SystemConfigUtil {

    /**
     * 各环节权限控制
     * @Description 系统启动的同时，将这些参数写入到内存中
     * @author Shindo   
     * @date 2016年12月21日 下午5:33:39
     */
    public void systemConfigInit() {
        try {
            CommonDao commonDao = (CommonDao) SpringUtil.getBean("commonDao");
            String queryRewardSql = "select * from t_system_config";
            Map<Integer, Object> rewardParam = new HashMap<Integer, Object>();
            JSONArray rewardArrary = commonDao.queryJSONArray(queryRewardSql, rewardParam, false);

            CommonContainer.saveData(CommonConstant.TER_12, rewardArrary);
            List data = (List) CommonContainer.getData(CommonConstant.TER_12);
            for (int i = 0; i < data.size(); i++) {
                Map map = (Map) data.get(i);
                String link = map.get("link").toString();
                String linkName = map.get("linkname").toString();
                System.out.println("link=" + link + ",linkName=" + linkName);

            }

            System.out.println("测试方法");
            System.out.println("测试方法尔 ");
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
