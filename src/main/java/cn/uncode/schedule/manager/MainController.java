package cn.uncode.schedule.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.uncode.schedule.ConsoleManager;
import cn.uncode.schedule.ZKScheduleManager;
import cn.uncode.schedule.core.TaskDefine;
import cn.uncode.schedule.model.TaskDefineShow;
import cn.uncode.schedule.util.HttpClientUtil;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/main")
public class MainController {
	private Logger log = LoggerFactory.getLogger(MainController.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 打开定时任务的管理页面
	 * @return
	 */
	@RequestMapping("/page")
	public String mPage(){
		return "manage";
	}
	
	/**
	 * 获取所有的定时任务的执行节点的信息
	 * @return
	 */
	@RequestMapping(value="/servers", method =RequestMethod.GET)
	@ResponseBody
	public String servers(){
		ZKScheduleManager manager = null;
		try {
			manager = ConsoleManager.getScheduleManager();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<String> servers = null;
		try {
			servers = manager.getScheduleDataManager().loadScheduleServerNames();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("获取到的所有的server node：{}", JSONObject.toJSONString(servers));
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		if(servers != null){
    		for(int i=0; i< servers.size();i++){
    			String ser = servers.get(i);
    			Map<String, String> server = new HashMap<String, String>();
    			server.put("serverName", ser);
				if(manager.getScheduleDataManager().isLeader(ser, servers)){ //是调度节点
					server.put("isLeader", "1");
				}else{
					server.put("isLeader", "0");
				}
				result.add(server);
    		}
		}
		return JSONObject.toJSONString(result);
	}
	
	/**
	 * 获取所有的定时任务执行节点的ip
	 * @return
	 */
	@RequestMapping(value="/server/ips", method =RequestMethod.GET)
	@ResponseBody
	public String serverIps(){
		List<String> serverIps = null;
		try {
			serverIps = ConsoleManager.getServerIps();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		log.info("获取到的所有的server ip：{}", JSONObject.toJSONString(serverIps));
		return JSONObject.toJSONString(serverIps);
	}
	
	/**
	 * 获取所有的定时任务
	 * @return
	 */
	@RequestMapping(value="/tasks", method =RequestMethod.GET)
	@ResponseBody
	public String tasks(){
		List<TaskDefineShow> tasksShow = new ArrayList<TaskDefineShow>();
		List<TaskDefine> tasks = ConsoleManager.queryScheduleTask();
		log.info("获取到的所有的task list：{}", JSONObject.toJSONString(tasks));
		
		for(TaskDefine task : tasks){
			TaskDefineShow taskDefineShow = new TaskDefineShow(task);
			if(task.getLastRunningTime() > 0){
				taskDefineShow.setLastRunningTimeShow(sdf.format(task.getLastRunningTime()));
			}else{
				taskDefineShow.setLastRunningTimeShow("-");
			}
			
			if(task.getStartTime() != null){
				taskDefineShow.setStartTimeShow(sdf.format(task.getStartTime()));
			}else{
				taskDefineShow.setStartTimeShow("-");
			}
			
			if(task.getCronExpression() == null){
				taskDefineShow.setCronExpression("-");
			}
			
			tasksShow.add(taskDefineShow);
		}
		return JSONObject.toJSONString(tasksShow);
	}
	
	/**
	 * 定时任务的删除
	 * @param targetBean
	 * @param targetMethod
	 * @return
	 */
	@RequestMapping(value="/task/del", method =RequestMethod.GET)
	@ResponseBody
	public String delTask(String targetBean, String targetMethod){
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(targetBean) || StringUtils.isEmpty(targetMethod)){
			result.put("returnCode", "9999");
			result.put("returnMsg", "bean or method is null");
			return result.toJSONString();
		}
		TaskDefine taskDefine = new TaskDefine();
		taskDefine.setTargetBean(targetBean);
		taskDefine.setTargetMethod(targetMethod);
		ConsoleManager.delScheduleTask(taskDefine);
		
		result.put("returnCode", "0000");
		result.put("returnMsg", "del success");
		return result.toJSONString();
	}
	
	/**
	 * 定时任务的添加
	 * ajax 请求的 contentType : "application/x-www-form-urlencoded"
	 * @param taskDefine
	 * @return
	 */
	@RequestMapping(value="/task", method =RequestMethod.POST)
	@ResponseBody
	public String addTask(@ModelAttribute TaskDefineShow taskDefine){
		log.info("添加定时任务：{}", JSONObject.toJSONString(taskDefine));
		JSONObject result = new JSONObject();
		taskDefine.setType(TaskDefine.TASK_TYPE_UNCODE);
		if(StringUtils.isEmpty(taskDefine.getTargetBean()) || StringUtils.isEmpty(taskDefine.getTargetMethod())){
			result.put("returnCode", "9999");
			result.put("returnMsg", "task bean or method is null");
			return result.toJSONString();
		}
		if(StringUtils.isNotEmpty(taskDefine.getCronExpression()) && StringUtils.isNotEmpty(taskDefine.getPeriodShow())){
			result.put("returnCode", "9999");
			result.put("returnMsg", "task run time is confict");
			return result.toJSONString();
		}
		if(StringUtils.isEmpty(taskDefine.getCronExpression()) && StringUtils.isEmpty(taskDefine.getPeriodShow())){
			result.put("returnCode", "9999");
			result.put("returnMsg", "task run time is not exist");
			return result.toJSONString();
		}
		
		try {
			if(StringUtils.isNotEmpty(taskDefine.getPeriodShow())){
				taskDefine.setPeriod(Long.parseLong(taskDefine.getPeriodShow()));
			}
			if(StringUtils.isNotEmpty(taskDefine.getStartTimeShow())){
				taskDefine.setStartTime(sdf.parse(taskDefine.getStartTimeShow()));
			}
			if(StringUtils.isEmpty(taskDefine.getCronExpression())){
				taskDefine.setCronExpression(null);
			}
			if(StringUtils.isEmpty(taskDefine.getParams())){
				taskDefine.setParams(null);
			}
			ConsoleManager.addScheduleTask(taskDefine);
		} catch (Exception e) {
			log.error(String.format("添加定时任务：%s", JSONObject.toJSONString(taskDefine)), e);
			result.put("returnCode", "9999");
			result.put("returnMsg", "task add exception");
			return result.toJSONString();
		}
		
		result.put("returnCode", "0000");
		result.put("returnMsg", "add success");
		return result.toJSONString();
	}
	
	/**
	 * 定时任务的手动执行方法
	 * @param executeUrl
	 * @param bean
	 * @param method
	 * @param param
	 * @return
	 */
	@RequestMapping(value="/task/execute", method =RequestMethod.POST)
	@ResponseBody
	public String runTask(String executeUrl, String bean, String method, String params){
		String url = executeUrl + "?bean=" + bean + "&method=" + method + "&param=" + params;
		log.info("手动执行定时任务的url：{}", url);
		JSONObject result = new JSONObject();
		try {
			return HttpClientUtil.httpPost(url);
		} catch (Exception e) {
			log.error("手动执行出现异常：", e);
			result.put("returnCode", "9999");
			result.put("returnMsg", "manual execute task exception");
		}
		return result.toJSONString();
	}
	
}
