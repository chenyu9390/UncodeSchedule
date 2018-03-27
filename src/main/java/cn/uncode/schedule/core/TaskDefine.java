package cn.uncode.schedule.core;

import java.util.Date;

/**
 * 任务定义，提供关键信息给使用者
 * @author juny.ye
 *
 */
public class TaskDefine {
	
	public static final String TASK_TYPE_UNCODE="uncode task";
	public static final String TASK_TYPE_QS="quartz/spring task";
	public static final String TASK_TYPE_QSD="quartz/spring task delay";
	
    /**
     * 目标bean
     */
    private String targetBean;
    
    /**
     * 目标方法
     */
    private String targetMethod;
    
    /**
     * cron表达式
     */
    private String cronExpression;
	
	/**
	 * 开始时间
	 */
	private Date startTime;
	
	/**
	 * 周期（毫秒）
	 */
	private long period;
	
	private String currentServer;
	
	/**
	 * 参数
	 */
	private String params;
	
	/**
	 * 类型
	 */
	private String type;
	
	private int runTimes;
	
	private long lastRunningTime;
	
	public boolean begin(Date sysTime) {
		return null != sysTime && sysTime.after(startTime);
	}

	public String getTargetBean() {
		return targetBean;
	}

	public void setTargetBean(String targetBean) {
		this.targetBean = targetBean;
	}

	public String getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getCurrentServer() {
		return currentServer;
	}

	public void setCurrentServer(String currentServer) {
		this.currentServer = currentServer;
	}
	
	public String stringKey(){
		return getTargetBean() + "#" + getTargetMethod();
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRunTimes() {
		return runTimes;
	}

	public void setRunTimes(int runTimes) {
		this.runTimes = runTimes;
	}

	public long getLastRunningTime() {
		return lastRunningTime;
	}

	public void setLastRunningTime(long lastRunningTime) {
		this.lastRunningTime = lastRunningTime;
	}
	
	
}