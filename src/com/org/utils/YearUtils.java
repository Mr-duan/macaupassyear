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

        }
    }

    /**
     * 
     * @param timeInterval
     */
    public static void autoRun() {
        // ͬʱ��һ����ʱ����,ÿ��Сʱִ��һ��
        Calendar calendar = Calendar.getInstance();

        Long timeInterval = Long.valueOf(PropertiesUtil.getValue("wx", "time_interval"));
        Date date = calendar.getTime(); // ��һ��ִ�ж�ʱ�����ʱ��
        YearUtilsTask task = new YearUtilsTask();
        // ����ָ����������ָ����ʱ�俪ʼ�����ظ��Ĺ̶��ӳ�ִ�С�
        timer.schedule(task, date, timeInterval * 1000);
    }
}
