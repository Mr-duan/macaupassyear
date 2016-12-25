package com.org.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.org.container.CommonContainer;

public class YearUtils2 {
    private static Timer timer = new Timer();

    private static class YearUtilsTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("*************test************");
            new YearUtils2().testCache();
        }
    }

    public void testCache() {
        try {
            System.out.println("----------------测试异步线程获取内存数据----------------");
            String linkValue1 = (String) CommonContainer.getData("link_1");
            System.out.println("=====异步线程获取内存的值：link 1 = " + linkValue1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param timeInterval
     */
    public void autoRun() {
        // 同时启一个定时任务,每两小时执行一次
        Calendar calendar = Calendar.getInstance();

        Long timeInterval = Long.valueOf(PropertiesUtil.getValue("wx", "time_interval"));
        Date date = calendar.getTime(); // 第一次执行定时任务的时间
        YearUtilsTask task = new YearUtilsTask();
        // 安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(task, date, timeInterval * 1000);
    }
}
