package com.liaowf.androidpc.androidclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

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
		
		
		return this.sendCommond(commandArg.computerInfos, commandArg.port, commandArg.command);

	}

	@Override
	protected void onCancelled() {
		// 取消处理任务
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(CommandResultInfo result) {
		// 处理任务完成之后
		Toast.makeText(androidClient, result.msg, Toast.LENGTH_SHORT).show();
		this.androidClient.getStateTxtView().setText(result.msg);
		
	}

	@Override
	protected void onPreExecute() {
		// 处理任务之前
		Toast.makeText(androidClient, "正在给所选控制电脑发送命令......", Toast.LENGTH_SHORT).show();
		this.androidClient.getStateTxtView().setText("正在给所选控制电脑发送命令......");

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 处理任务当中的进度
		super.onProgressUpdate(values);
	}
	
	
	
	private CommandResultInfo sendCommond(List<ComputerInfo> cpList,int port,String commond) {

		InputStream in = null;
		OutputStream out = null;

		Socket cSocket = null;
		
		CommandResultInfo resInfo1 = new CommandResultInfo();
		resInfo1.rs = true;
		resInfo1.msg = "";
		
		
		if(cpList==null || (cpList.size()==0)){
			resInfo1.rs = false;
			resInfo1.msg = "没有选择控制电脑，请先获取控制电脑。";
			
			return resInfo1;
			
		}
		
		for(int i=0;i<cpList.size();i++){
			
			CommandResultInfo resInfo = new CommandResultInfo();
			resInfo.rs = true;
			resInfo.msg = "";
			
			ComputerInfo c = (ComputerInfo) cpList.get(i);
			
			try {
				cSocket = new Socket(c.IP, port);

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
				resInfo.msg = "命令发送到控制电脑 "+c.IP+"-"+c.hostName+" 出现错误(未知主机)："+e.getMessage()+";";
            
			} catch (IOException e) {
				e.printStackTrace();
				resInfo.rs = false;
				resInfo.msg = "命令发送到控制电脑 "+c.IP+"-"+c.hostName+" 出现错误(传输出错)："+e.getMessage()+";";

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
			
			if(resInfo.rs==true){
				resInfo.msg ="命令发送到控制电脑 "+c.IP+"-"+c.hostName+" 成功;";
				resInfo.rs = true;
			}		
			
			resInfo1.msg+=resInfo.msg;
		}
		
		
		resInfo1.rs = true;
		resInfo1.msg = "给所选控制电脑发送命令完成。执行情况："+resInfo1.msg;
 		
		return resInfo1;

	}

}
