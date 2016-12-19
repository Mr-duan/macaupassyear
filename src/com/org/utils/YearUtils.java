package com.org.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class YearUtils {
    private static Timer timer = new Timer();

    private static class YearUtilsTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("*************test************");
            new YearUtils().testCache();
        }
    }

    public void testCache() {
        try {
            Memcache memcache = Memcache.getInstance();
            memcache.setValue("One", "test");

            String value = memcache.getValue("One");
            System.out.println("初始值：" + value);

            memcache.setValue("One", "test*******Test");
            value = memcache.getValue("One");
            System.out.println("修改后的值：" + value);
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
