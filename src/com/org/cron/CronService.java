package com.org.cron;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.org.exception.BusinessException;

/**
 * @author Nano
 * 
 * 定时服务的抽象
 */
public abstract class CronService implements Runnable {

    private Log log = LogFactory.getLog(CronServiceFactory.class);

    protected String cronDate;

    protected String cronDateTime;

    public String getCronDate() {
        return this.cronDate;
    }

    public void setCronDate(String cronDate) {
        this.cronDate = cronDate;
    }

    public String getCronDateTime() {
        return this.cronDateTime;
    }

    public void setCronDateTime(String cronDateTime) {
        this.cronDateTime = cronDateTime;
    }

    public abstract void service() throws BusinessException;

    @Override
    public void run() {
        try {
            this.log.info("开始执行服务(" + this.getClass().getName() + ")");
            this.service();
            this.log.info("结束执行服务(" + this.getClass().getName() + ")");
        } catch (BusinessException ce) {
            ce.printStackTrace();
        }
    }

}
