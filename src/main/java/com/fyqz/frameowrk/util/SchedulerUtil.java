package com.fyqz.frameowrk.util;

import com.fyqz.frameowrk.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @Title: SchedulerUtil
 * @ProjectName: fyqz-platform
 * @Description: TODO
 * @author: zengchao
 * @date: 2019/5/16 17:21
 */
public class SchedulerUtil {
    private static Logger logger = LogManager.getLogger();
    private static StdSchedulerFactory sf = null;
    private static Scheduler sd = null;

    /**
     * <p>Title: createTask</p>
     * <p>Description: 创建任务 </p>
     *
     * @param processId 进程id
     * @param allBound  回合数
     * @param className 执行类的名称
     * @version v5.0
     */
    public static void createTask(String processId, Integer allBound,  String className) {
        try {
            Scheduler scheduler = getScheduler();
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("processId", processId);
            jobDataMap.put("allBound", allBound);
            JobDetail jobDetail = registerTask(processId, Constants.TIME_GROUP, className, jobDataMap);
            Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?"))
                    .withIdentity(processId, Constants.TIME_GROUP).forJob(jobDetail)
                    .usingJobData(jobDataMap).build();
            scheduler.scheduleJob(trigger);
            scheduler.start();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            logger.error("Try to create Job cause error : ", e);
        }
    }

    /**
     * <p>Title: delTask</p>
     * <p>Description: 删除任务 </p>
     *
     * @param jobName
     * @version: v2.0
     * @author: 曾超
     * @date: 2018年6月27日 下午5:15:18
     */
    public static void delTask(String jobName) {
        Scheduler scheduler = SchedulerUtil.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobName, Constants.TIME_GROUP);
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, Constants.TIME_GROUP);
        try {
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(jobKey);//删除任务
        } catch (SchedulerException e) {
            logger.error("Try to del Job cause error : ", e);
        }
    }

    /**
     * <p>Title: stopJob</p>
     * <p>Description: 暂停任务 </p>
     *
     * @param jobName 组名
     * @version: v2.0
     * @author: 曾超
     * @date: 2018年6月27日 下午5:22:49
     */
    public static void stopJob(String jobName) {
        Scheduler scheduler = SchedulerUtil.getScheduler();
        try {
            JobKey jobKey = JobKey.jobKey(jobName, Constants.TIME_GROUP);
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            logger.error("Try to stop Job cause error : ", e);
        }
    }

    /**
     * <p>Title: resumeJob</p>
     * <p>Description: 恢复任务 </p>
     *
     * @version: v2.0
     * @author: 曾超
     * @date: 2018年6月29日 上午11:33:06
     */
    public static void resumeJob(String jobName) {
        Scheduler scheduler = SchedulerUtil.getScheduler();
        try {
            JobKey jobKey = JobKey.jobKey(jobName, Constants.TIME_GROUP);
            scheduler.resumeJob(jobKey);
        } catch (Exception e) {
            logger.error("Try to resumeJob Job cause error : ", e);
        }
    }

    /**
     * <p>Title: getJobState</p>
     * <p>Description: 获取任务状态 </p>
     *
     * @version: v2.0
     * @author: 曾超
     * @date: 2018年6月27日 下午5:59:59
     * BLOCKED 4 阻塞; COMPLETE 2 完成; ERROR 3 错误; NONE -1 不存在; NORMAL 0 正常; PAUSED 1 暂停;
     */
    public static String getJobState(String processId) {
        String state = "-1";
        try {
            Scheduler scheduler = SchedulerUtil.getScheduler();
            GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupContains("");
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeys) {
                if ((jobKey.getGroup() + "." + jobKey.getName()).equals(Constants.TIME_GROUP + "." + processId)) {
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    Trigger trigger = triggers.get(0);
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    state = triggerState.name();
                    break;
                }
            }
        } catch (SchedulerException e) {
            logger.error("Try to getJobState  cause error : ", e);
        }
        return state;
    }

    /**
     * <p>Description: 定义job实列 </p>
     *
     * @param jobName    任务名
     * @param jobGroup   组名
     * @param className  执行类的名称
     * @param jobDataMap 任务参数
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    private static JobDetail registerTask(String jobName, String jobGroup, String className, JobDataMap jobDataMap) throws SchedulerException, ClassNotFoundException {
        Scheduler scheduler = getScheduler();
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            return scheduler.getJobDetail(jobKey);
        } else {
            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(className);
            JobDetail jobDetail = JobBuilder.newJob(clazz)
                    .withIdentity(jobKey)
                    .storeDurably(true)
                    .usingJobData(jobDataMap)
                    .build();
            scheduler.addJob(jobDetail, true);
            return jobDetail;
        }
    }

    /**
     * <p>Description: 创建时间工厂连接 </p>
     *
     * @return 时间工厂连接
     */
    private static synchronized StdSchedulerFactory getFactory() {
        if (sf == null) {
            try {
                sf = new StdSchedulerFactory();
                Properties props = new Properties();
                props.put("org.quartz.scheduler.instanceName", "fyqzSchedule");
                props.put("org.quartz.threadPool.threadCount", "100");
                props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
                sf.initialize(props);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        return sf;
    }

    private static synchronized Scheduler getScheduler() {
        if (sd == null) {
            StdSchedulerFactory factory = getFactory();
            try {
                sd = factory.getScheduler();
                return sd;
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        return sd;
    }
}
