package com.liaowf.androidpc.androidclient;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class AndroidClient extends Activity {
	
	
	private Button connwifiButton = null,getipButton = null,shutdownButton = null,rebootButton = null,logoffButton = null;
	
	private TextView iphostnameTxtView = null;
	
	
    private String command_ip = "";
    private int command_port = 30000;
    
    public TextView getIphostnameTxtView(){
    	return this.iphostnameTxtView;
    }
    
    public void setCommand_ip(String _command_ip){
    	this.command_ip = _command_ip;
    }
    
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
  
        shutdownButton = (Button)this.findViewById(R.id.button_shutdown);
        rebootButton = (Button)this.findViewById(R.id.button_reboot);
        logoffButton = (Button)this.findViewById(R.id.button_logoff);
        
        getipButton = (Button)this.findViewById(R.id.button_getip);
        
        iphostnameTxtView = (TextView)this.findViewById(R.id.textView_iphostname);
        
        connwifiButton = (Button)this.findViewById(R.id.button_connwifi);
        
        connwifiButton.setEnabled(false);

        getipButton.setOnClickListener(new OnClickListener(){

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
				
				CommandAsyncTask task = new CommandAsyncTask(AndroidClient.this);			
				CommandArg arg = new CommandArg();
				arg.ip = command_ip;
				arg.port = command_port;
				arg.command = Config.shutdown_cmd;
				task.execute(arg);
					
			}
        });
        
        rebootButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
	            CommandAsyncTask task = new CommandAsyncTask(AndroidClient.this);				
				CommandArg arg = new CommandArg();				
				arg.ip = command_ip;
				arg.port = command_port;
				arg.command = Config.reboot_cmd;
				task.execute(arg);
				
			}
        });
        
        logoffButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				CommandAsyncTask task = new CommandAsyncTask(AndroidClient.this);				
				CommandArg arg = new CommandArg();				
				arg.ip = command_ip;
				arg.port = command_port;
				arg.command = Config.logoff_cmd;
				task.execute(arg);
				
			}
        	
        });
        
    }
    
    
	
}