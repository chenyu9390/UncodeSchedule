package cn.uncode.schedule.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * 调度处理工具类
 * 
 * @author juny.ye
 *
 */
public class ScheduleUtil {
    public static String OWN_SIGN_BASE ="BASE";

    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "";
        }
    }

    public static int getFreeSocketPort() {
        try {
            ServerSocket ss = new ServerSocket(0);
            int freePort = ss.getLocalPort();
            ss.close();
            return freePort;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    public static String transferDataToString(Date d){
        SimpleDateFormat DATA_FORMAT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DATA_FORMAT_yyyyMMddHHmmss.format(d);
    }
    public static Date transferStringToDate(String d) throws ParseException{
        SimpleDateFormat DATA_FORMAT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DATA_FORMAT_yyyyMMddHHmmss.parse(d);
    }
    public static Date transferStringToDate(String d,String formate) throws ParseException{
        SimpleDateFormat FORMAT = new SimpleDateFormat(formate);
        return FORMAT.parse(d);
    }
    public static String getTaskTypeByBaseAndOwnSign(String baseType,String ownSign){
        if(ownSign.equals(OWN_SIGN_BASE)){
            return baseType;
        }
        return baseType+"$" + ownSign;
    }
    public static String splitBaseTaskTypeFromTaskType(String taskType){
         if(taskType.contains("$")){
             return taskType.substring(0,taskType.indexOf("$"));
         }else{
             return taskType;
         }
         
    }
    public static String splitOwnsignFromTaskType(String taskType){
         if(taskType.contains("$")){
             return taskType.substring(taskType.indexOf("$")+1);
         }else{
             return OWN_SIGN_BASE;
         }
    }
    
    public static String getTaskNameFormBean(String beanName, String methodName){
    	return beanName + "#" + methodName;
    }
    
    /**
     * 分配任务数量
     * @param serverNum 总的服务器数量
     * @param taskItemNum 任务项数量
     * @param maxNumOfOneServer 每个server最大任务项数目
     * @return null
     */
    public static int[] assignTaskNumber(int serverNum,int taskItemNum,int maxNumOfOneServer){
        int[] taskNums = new int[serverNum];
        int numOfSingle = taskItemNum / serverNum;
        int otherNum = taskItemNum % serverNum;
        if (maxNumOfOneServer >0 && numOfSingle >= maxNumOfOneServer) {
            numOfSingle = maxNumOfOneServer;
            otherNum = 0;
        }
        for (int i = 0; i < taskNums.length; i++) {
            if (i < otherNum) {
                taskNums[i] = numOfSingle + 1;
            } else {
                taskNums[i] = numOfSingle;
            }
        }
        return taskNums;
    }
    private static String printArray(int[] items){
        String s="";
        for(int i=0;i<items.length;i++){
            if(i >0){s = s +",";}
            s = s + items[i];
        }
        return s;
    }
    
    static Properties prop = new Properties();
	static {
		try {
			InputStream in = ScheduleUtil.class.getResourceAsStream("/schedule.properties");
			prop.load(in);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	/**
	 * 从配置文件获取server code
	 * @return
	 */
	public static String getServerCode(){
		return prop.getProperty("uncode.schedule.server.code");
	}
	
	/**
	 * 从zk的server list中获取server ip list ，保持元素顺序
	 * @param serverList
	 * @return
	 */
	public static List<String> getServerIpList(List<String> serverList){
		List<String> serverIpList = new ArrayList<String>(serverList.size());
		for(String ser:serverList){
			serverIpList.add(ser.substring(0, ser.indexOf("$")));
		}
		return serverIpList;
	}
	
    public static void main(String[] args) {
        System.out.println(printArray(assignTaskNumber(1,10,0)));
        System.out.println(printArray(assignTaskNumber(2,10,0)));
        System.out.println(printArray(assignTaskNumber(3,10,0)));
        System.out.println(printArray(assignTaskNumber(4,10,0)));
        System.out.println(printArray(assignTaskNumber(5,10,0)));
        System.out.println(printArray(assignTaskNumber(6,10,0)));
        System.out.println(printArray(assignTaskNumber(7,10,0)));
        System.out.println(printArray(assignTaskNumber(8,10,0)));       
        System.out.println(printArray(assignTaskNumber(9,10,0)));
        System.out.println(printArray(assignTaskNumber(10,10,0)));
        
        System.out.println("-----------------");
        
        System.out.println(printArray(assignTaskNumber(1,10,3)));
        System.out.println(printArray(assignTaskNumber(2,10,3)));
        System.out.println(printArray(assignTaskNumber(3,10,3)));
        System.out.println(printArray(assignTaskNumber(4,10,3)));
        System.out.println(printArray(assignTaskNumber(5,10,3)));
        System.out.println(printArray(assignTaskNumber(6,10,3)));
        System.out.println(printArray(assignTaskNumber(7,10,3)));
        System.out.println(printArray(assignTaskNumber(8,10,3)));       
        System.out.println(printArray(assignTaskNumber(9,10,3)));
        System.out.println(printArray(assignTaskNumber(10,10,3)));
        
    }
}