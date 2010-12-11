package com.liaowf.androidpc.androidclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
			Toast.makeText(this.androidClient, "接收服务端IP失败，原因:"+result.msg, Toast.LENGTH_LONG).show();		
			this.androidClient.getIphostnameTxtView().setText(result.msg);
		}else {	
			Toast.makeText(this.androidClient, "接收IP:"+result.IP+" HOSTNAME:"+result.hostName, Toast.LENGTH_LONG).show();		
			this.androidClient.getIphostnameTxtView().setText("接收IP:"+result.IP+" HOSTNAME:"+result.hostName);
			
			this.androidClient.setCommand_ip(result.IP);
		}
		
	}

	@Override
	protected void onPreExecute() {

		Toast.makeText(this.androidClient, "正在接收服务端IP......", Toast.LENGTH_LONG).show();
		this.androidClient.getIphostnameTxtView().setText("正在接收服务端IP......");
		
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
	
	
	  private UDPIPHostNameResultInfo udpIPHostname(String broadcast_ip,int broadcast_port,String command){
	    	
	    	
		    UDPIPHostNameResultInfo resInfo = new UDPIPHostNameResultInfo();
		  
	    	resInfo.rs = false;
	    	resInfo.msg = "";
	    	resInfo.IP = "";
	    	resInfo.hostName = "";
	    	
		    DatagramSocket ds = null;
			
			try {
				ds = new DatagramSocket();
			
				// 设置超时时间
				ds.setSoTimeout(Config.connection_timeout);
				
			   // String getipcmd = "cmd:android->pc:getiphostname";
				
				String getipcmd = command;
			    DatagramPacket dp = new DatagramPacket(getipcmd.getBytes(),getipcmd.length(),InetAddress.getByName(broadcast_ip),broadcast_port);
			    ds.send(dp);
			    		    
		//	    byte[] buf = new byte[512];
		//	    DatagramPacket dp2 = new DatagramPacket(buf,buf.length);
			    
			    ds.receive(dp);
				String str = new String(dp.getData());
				
				String ip_hostname = "";
				if(str!=null && !"".equals(str.trim())){
					ip_hostname = str.trim();
					
					int _i=ip_hostname.indexOf("-");
					
					resInfo.IP = ip_hostname.substring(0,_i);
					resInfo.hostName = ip_hostname.substring(_i+1);
									
					resInfo.rs = true;
					resInfo.msg = "";
				}else {
					resInfo.rs = false;
					resInfo.msg ="无法 接收到IP, 接收到IP 为空";
				}
				

				//System.out.println(str2);
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

}
