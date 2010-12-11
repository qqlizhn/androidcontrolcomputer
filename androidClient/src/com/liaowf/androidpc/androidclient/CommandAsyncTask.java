package com.liaowf.androidpc.androidclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.widget.Toast;

public class CommandAsyncTask extends android.os.AsyncTask<CommandArg,Integer,CommandResultInfo>{

	private AndroidClient androidClient = null;
	
	public CommandAsyncTask(AndroidClient _androidClient){
		this.androidClient = _androidClient;
	}
	
	
	@Override
	protected CommandResultInfo doInBackground(CommandArg... arg0) {
		// 后台处理任务		
		CommandArg commandArg = arg0[0];
		return this.sendCommond(commandArg.ip, commandArg.port, commandArg.command);

	}

	@Override
	protected void onCancelled() {
		// 取消处理任务
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(CommandResultInfo result) {
		// 处理任务完成之后
		Toast.makeText(androidClient, result.msg, Toast.LENGTH_LONG).show();
		this.androidClient.getIphostnameTxtView().setText(result.msg);
		
	}

	@Override
	protected void onPreExecute() {
		// 处理任务之前
		Toast.makeText(androidClient, "正在发送命令......", Toast.LENGTH_LONG).show();
		this.androidClient.getIphostnameTxtView().setText("正在发送命令......");

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 处理任务当中的进度
		super.onProgressUpdate(values);
	}
	
	
	
	private CommandResultInfo sendCommond(String host,int port,String commond) {

		InputStream in = null;
		OutputStream out = null;

		Socket cSocket = null;
		
		CommandResultInfo resInfo = new CommandResultInfo();
		resInfo.rs = false;
		resInfo.msg = "";
		
		
		if(host==null || "".equals(host)){
			resInfo.rs = false;
			resInfo.msg = "IP 为空，请先获取IP。";
			
			return resInfo;
			
		}
		
 		try {
			cSocket = new Socket(host, port);

			// 设置超时时间
			cSocket.setSoTimeout(Config.connection_timeout);
			if (cSocket != null) {
				out = cSocket.getOutputStream();
				in = cSocket.getInputStream();

				String data = commond;
				out.write(data.getBytes());

			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
			resInfo.rs = false;
			resInfo.msg = "命令发送出现错误(未知主机)："+e.getMessage();
			return resInfo;
		} catch (IOException e) {
			e.printStackTrace();
			resInfo.rs = false;
			resInfo.msg = "命令发送出现错误(传输出错)："+e.getMessage();
			return resInfo;
		} finally {
			if (cSocket != null) {
				try {
					cSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		resInfo.msg ="命令发送成功";
		resInfo.rs = true;
		
		return resInfo;

	}

}
