package com.liaowf.androidpc.androidclient;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AndroidClient extends Activity {
	
	
	private LinearLayout linearlayout_layout = null;
	
	private Button connwifiButton = null,getpcButton = null,shutdownButton = null,rebootButton = null,logoffButton = null;
	
	private TextView stateTxtView = null;
	

    private int command_port = 30000;
    
    public TextView getStateTxtView(){
    	return this.stateTxtView;
    }
    
    
    public LinearLayout getLinearLayout(){
    	return this.linearlayout_layout;
    }
    
   

    
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
  
        shutdownButton = (Button)this.findViewById(R.id.button_shutdown);
        rebootButton = (Button)this.findViewById(R.id.button_reboot);
        logoffButton = (Button)this.findViewById(R.id.button_logoff);
        
        getpcButton = (Button)this.findViewById(R.id.button_getpc);
        
        stateTxtView = (TextView)this.findViewById(R.id.textView_statetext);
        
        connwifiButton = (Button)this.findViewById(R.id.button_connwifi);
        
        connwifiButton.setEnabled(false);
        
        linearlayout_layout = (LinearLayout)this.findViewById(R.id.linearlayout_layout);
        
        
        
        getpcButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
												
				UDPIPHostNameAsyncTask task = new UDPIPHostNameAsyncTask(AndroidClient.this);
				UDPIPHostNameArg arg = new UDPIPHostNameArg();
				arg.broadcast_ip = Config.broadcast_ip;
				arg.broadcast_port = Config.broadcast_port;
				arg.command = Config.iphostname_cmd;		
				task.execute(arg);

			}
        	       	
        });
        
        
        shutdownButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				AndroidClient.this.onclickCommand(Config.shutdown_cmd);
								
			}
        });
        
        rebootButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				AndroidClient.this.onclickCommand(Config.reboot_cmd);
				
			}
        });
        
        logoffButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				AndroidClient.this.onclickCommand(Config.logoff_cmd);
				
				
			}
        	
        });
        
    }
    
    
    private void onclickCommand(String command){
    	CommandAsyncTask task = new CommandAsyncTask(AndroidClient.this);			
		CommandArg arg = new CommandArg();
		
		List<ComputerInfo> cList = getSelectComputer();
		if(cList==null || cList.size()==0){
			Toast.makeText(AndroidClient.this, "你没有选择控制电脑", Toast.LENGTH_SHORT).show();
			stateTxtView.setText("你没有选择控制电脑");
		}else {
			arg.computerInfos = cList;
			arg.port = command_port;
			arg.command = command;
			task.execute(arg);
		}
    }
    
    
    
    private List<ComputerInfo> getSelectComputer(){
    	
    	List<ComputerInfo> cList = new ArrayList<ComputerInfo>();
    	
    	int childcount = linearlayout_layout.getChildCount();
		
		for(int i=0;i<childcount;i++){
		   View view =  linearlayout_layout.getChildAt(i);
		   if(view instanceof CheckBox){
			   CheckBox _checkbox = (CheckBox)view;
			   String _str = _checkbox.getText().toString();
			   int _i = _str.indexOf("-");
			   if((_i!=-1) && _checkbox.isChecked()==true){
				   
				   ComputerInfo c = new ComputerInfo();
				   c.IP = _str.substring(0,_i);
				   c.hostName = _str.substring(_i+1);
				   cList.add(c);
			   }
		   }
		}
    	
		return cList;
    }
    
	
}