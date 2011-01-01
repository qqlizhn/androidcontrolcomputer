package com.liaowf.androidpc.androidclient;

public class Config {

	public static String broadcast_ip = "255.255.255.255";  // 广播IP
	
	public static int broadcast_port = 20000; // 广播端口
	
	
	
	public static int connection_timeout = 30*1000; // 通讯超时设置  (毫秒)
	
	
	public static String iphostname_cmd = "cmd:android->pc:getiphostname"; // 获取 IP 命令
	
	public static String shutdown_cmd = "cmd:android->pc:shutdown";   // 关机命令
	
	public static String reboot_cmd = "cmd:android->pc:reboot";  // 重启命令
	
	public static String logoff_cmd = "cmd:android->pc:logoff";  // 注销命令
	
	
	
	
	
}
