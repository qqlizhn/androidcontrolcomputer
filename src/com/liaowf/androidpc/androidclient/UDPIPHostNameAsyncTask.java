package com.liaowf.androidpc.androidclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UDPIPHostNameAsyncTask extends android.os.AsyncTask<UDPIPHostNameArg,Integer,UDPIPHostNameResultInfo>{
	
	
	private AndroidClient androidClient = null;
	
	
	public UDPIPHostNameAsyncTask(AndroidClient _androidClient){
		this.androidClient = _androidClient;
		
	}

	@Override
	protected UDPIPHostNameResultInfo doInBackground(UDPIPHostNameArg... params) {
		UDPIPHostNameArg arg = params[0];
		
		return this.udpIPHostname(arg.broadcast_ip,arg.broadcast_port,arg.command);
		
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(UDPIPHostNameResultInfo result) {
      
		if(result.rs==false){
			Toast.makeText(this.androidClient, "获取控制电脑失败，原因:"+result.msg, Toast.LENGTH_SHORT).show();		
			this.androidClient.getStateTxtView().setText("获取控制电脑失败，原因:"+result.msg);

		}else {	
			
			
			
			//this.androidClient.setCommand_ip(result.IP);
			
			// 重画 被控制的电脑列表 , 去重
			
			LinearLayout linearlayout_layout = this.androidClient.getLinearLayout();
					        	
			
			String checkBoxText = result.computers.IP+"-"+result.computers.hostName;
		    // 不存在，才增加
			
			int childcount = linearlayout_layout.getChildCount();
			
			boolean isExist = false;
			for(int i=0;i<childcount;i++){
			   View view =  linearlayout_layout.getChildAt(i);
			   if(view instanceof CheckBox){
				   CheckBox _checkbox = (CheckBox)view;
				   String _str = _checkbox.getText().toString();
				   if(checkBoxText.equals(_str)){
					   // 存在
					   isExist = true;
					   break;
				   }
			   }
			}
			
			if (isExist == false) {
				CheckBox checkbox = new CheckBox(this.androidClient);
				checkbox.setText(checkBoxText);
				checkbox.setChecked(false);
				linearlayout_layout.addView(checkbox, 1);
				
				Toast.makeText(this.androidClient, "获取控制电脑成功 IP:"+result.computers.IP+" 主机名:"+result.computers.hostName, Toast.LENGTH_SHORT).show();		
				this.androidClient.getStateTxtView().setText("获取控制电脑成功 IP:"+result.computers.IP+" 主机名:"+result.computers.hostName);
			
			}else {
				Toast.makeText(this.androidClient, "控制电脑已经获取过了  IP:"+result.computers.IP+" 主机名:"+result.computers.hostName, Toast.LENGTH_SHORT).show();		
				this.androidClient.getStateTxtView().setText("控制电脑已经获取过了 IP:"+result.computers.IP+" 主机名:"+result.computers.hostName);
			}
		}
		
	}

	@Override
	protected void onPreExecute() {

		Toast.makeText(this.androidClient, "正在获取控制电脑......", Toast.LENGTH_SHORT).show();
		this.androidClient.getStateTxtView().setText("正在获取控制电脑......");
		
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
	
	
	private UDPIPHostNameResultInfo udpIPHostname(String broadcast_ip,int broadcast_port,String command){
	    	
	    	
		    UDPIPHostNameResultInfo resInfo = new UDPIPHostNameResultInfo();
		  
	    	resInfo.rs = false;
	    	resInfo.msg = "";
	    	
	    	resInfo.computers = new ComputerInfo();
	    	
	    	
		    DatagramSocket ds = null;
			
			try {
				ds = new DatagramSocket();
			
				// 设置超时时间
				ds.setSoTimeout(Config.connection_timeout);
				
			   // String getipcmd = "cmd:android->pc:getiphostname";
				
				String getipcmd = command;
			    DatagramPacket dp = new DatagramPacket(getipcmd.getBytes(),getipcmd.length(),InetAddress.getByName(broadcast_ip),broadcast_port);
			    ds.send(dp);
			    		    
			    byte[] buf = new byte[1024];
			    DatagramPacket dp2 = new DatagramPacket(buf,buf.length);
			    
			    ds.receive(dp2);
			    
			    byte []data = dp2.getData();
			    
			    // 判断结束点
			    int len = 0;
			    for(int i=0;i<data.length;i++){
			    	if(data[i]!=0){
			    		len++;
			    	}else{
			    		break;
			    	}
			    }
	
				String str = new String(data,0,len);						
				Log.d("com.liaowf.androidpc.androidclient", "receive:"+str);
				
				String ips_hostname = "";
				if(str!=null && !"".equals(str.trim())){
					ips_hostname = str.trim();
					
					//int _i=ips_hostname.indexOf("-");
					
					Log.d("com.liaowf.androidpc.androidclient", ips_hostname);
					
					int _i = ips_hostname.lastIndexOf("#");
					
					resInfo.computers.hostName = ips_hostname.substring(_i+1);
					
					Log.d("com.liaowf.androidpc.androidclient", "receive hostname:"+resInfo.computers.hostName);
					
					resInfo.computers.IP = checkPCIP(ips_hostname.substring(0, _i),this.getIP());
					
					Log.d("com.liaowf.androidpc.androidclient", "receive max ip:"+resInfo.computers.IP);
					
					//resInfo.IP = ip_hostname.substring(0,_i);
					//resInfo.hostName = ip_hostname.substring(_i+1);
									
					resInfo.rs = true;
					resInfo.msg = "";
				}else {
					resInfo.rs = false;
					resInfo.msg ="无法 接收到IP, 接收到IP 为空";
				}
				
				  return resInfo;
			   
			}catch (SocketException e) {
				e.printStackTrace();
				resInfo.rs = false;
				resInfo.msg = "创建UDP 出错 "+e.getMessage();
				return resInfo;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				resInfo.rs = false;
				resInfo.msg = "创建UDP 出错 "+e.getMessage();
				return resInfo;
			} catch (IOException e) {
				e.printStackTrace();
				
				resInfo.rs = false;
				resInfo.msg = "UDP IO出错 "+e.getMessage();
				
				return resInfo;
				
			}finally{	
				if(ds!=null){
					ds.close(); //关闭连接
				}
			}
	    }
	
	// 计算出 对于 多IP 下的被控制电脑的合理IP
	private String checkPCIP(String substring, String ip) {
		
		StringTokenizer strToken = new StringTokenizer(substring,"#");
		
		int max = -1;
		String maxip = "";
		
		Log.d("com.liaowf.androidpc.androidclient", "check substring:"+substring);
		Log.d("com.liaowf.androidpc.androidclient", "check ip:"+ip);
		
		while(strToken.hasMoreElements()){
			String str = (String)strToken.nextElement();
			
			Log.d("com.liaowf.androidpc.androidclient", "check str:"+str);
			
			int c = c(str,ip);
			
			Log.d("com.liaowf.androidpc.androidclient", "check c:"+c);
			
			if(c>max){
				max = c;
				maxip = str;
			}	
		}
		
		return maxip;
	}
	
	private int c(String srcIP,String desIP){
		int c=0;
		
		StringTokenizer srcToken = new StringTokenizer(srcIP,".");
		StringTokenizer desToken = new StringTokenizer(desIP,".");

		while(srcToken.hasMoreElements() && desToken.hasMoreElements()){
		
			String src = (String)srcToken.nextElement();
			String des = (String)desToken.nextElement();
			Log.d("com.liaowf.androidpc.androidclient",src+" : "+des);
			if(src.equals(des)){
				c++;
			}
			
		}       
		return c;
	}

	// 获取本手机WIFI 下的IP地址
	private String getIP() {
    
        
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) this.androidClient.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) { 
            wifiManager.setWifiEnabled(true);   
        } 
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
        String ip = "";
        if(wifiInfo!=null){
        	int ipAddress = wifiInfo.getIpAddress();  
            ip = intToIp(ipAddress);  
        }
        return ip;
    }   
	
	
    private String intToIp(int i) {      
          return (i & 0xFF ) + "." +      
        ((i >> 8 ) & 0xFF) + "." +      
        ((i >> 16 ) & 0xFF) + "." +      
        ( i >> 24 & 0xFF) ;
     }  



}
