package com.org.utils;

import java.util.HashMap;
import java.util.Map;

import com.org.common.CommonConstant;
import com.org.container.CommonContainer;
import com.org.dao.CommonDao;
import com.org.util.SpringUtil;

import net.sf.json.JSONArray;

@SuppressWarnings("unchecked")
public class SystemConfigUtil {

    /**
     * 各环节权限控制
     * @Description 系统启动的同时，将这些参数写入到内存中
     * @author Shindo   
     * @date 2016年12月21日 下午5:33:39
     */
    public void systemConfigInit() {
        try {
            // 读取各环节系统配置信息，并存入内存
            CommonDao commonDao = (CommonDao) SpringUtil.getBean("commonDao");
            String queryConfigSql = "select * from t_system_config";
            Map<Integer, Object> configParam = new HashMap<Integer, Object>();
            JSONArray configArrary = commonDao.queryJSONArray(queryConfigSql, configParam, false);
            System.out.println("初始化各环节权限配置到内存中");
            for (int i = 0; i < configArrary.size(); i++) {

                Map<Object, Object> config = (Map<Object, Object>) configArrary.get(i);
                String link = config.get("link").toString();
                String status = config.get("status").toString();
                CommonContainer.saveData(CommonConstant.LINK + link, status);

                // 日志跟踪
                System.out.println(CommonConstant.LINK + link + " = " + CommonContainer.getData(CommonConstant.LINK + link));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
