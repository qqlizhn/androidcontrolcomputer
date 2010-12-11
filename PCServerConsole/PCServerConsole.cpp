// PCServerConsole.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"


// 关机
void ShutDown()
{
HANDLE hToken;
TOKEN_PRIVILEGES tkp;
//代开当前进程信令
if(!OpenProcessToken(GetCurrentProcess(),TOKEN_ADJUST_PRIVILEGES|TOKEN_QUERY,&hToken))
   return;
//获取本地文艺表示用于在特定的系统中设置权限
LookupPrivilegeValue(NULL,SE_SHUTDOWN_NAME,&tkp.Privileges[0].Luid);
tkp.PrivilegeCount=1;
tkp.Privileges[0].Attributes=SE_PRIVILEGE_ENABLED;
//提升访问令牌权限
AdjustTokenPrivileges(hToken,FALSE,&tkp,0,(PTOKEN_PRIVILEGES)NULL,0);
//关机，要实现重启只需将第一个参数换成EWX_REBOOT|EWX_FORCE
//注销第一个参数换成EWX_LOGOFF|EWX_FORCE
ExitWindowsEx(EWX_SHUTDOWN|EWX_FORCE,0);

}

//重启
void Reboot()
{
HANDLE hToken;
TOKEN_PRIVILEGES tkp;
if(!OpenProcessToken(GetCurrentProcess(),TOKEN_ADJUST_PRIVILEGES|TOKEN_QUERY,&hToken))
   return;
LookupPrivilegeValue(NULL,SE_SHUTDOWN_NAME,&tkp.Privileges[0].Luid);
tkp.PrivilegeCount=1;
tkp.Privileges[0].Attributes=SE_PRIVILEGE_ENABLED;
AdjustTokenPrivileges(hToken,FALSE,&tkp,0,(PTOKEN_PRIVILEGES)NULL,0);
    ExitWindowsEx(EWX_REBOOT|EWX_FORCE,0);

}

//注销
void LogOff()
{
HANDLE hToken;
TOKEN_PRIVILEGES tkp;
if(!OpenProcessToken(GetCurrentProcess(),TOKEN_ADJUST_PRIVILEGES|TOKEN_QUERY,&hToken))
   return;
LookupPrivilegeValue(NULL,SE_SHUTDOWN_NAME,&tkp.Privileges[0].Luid);
tkp.PrivilegeCount=1;
tkp.Privileges[0].Attributes=SE_PRIVILEGE_ENABLED;
AdjustTokenPrivileges(hToken,FALSE,&tkp,0,(PTOKEN_PRIVILEGES)NULL,0);
    ExitWindowsEx(EWX_LOGOFF|EWX_FORCE,0);

}



// UDP 包广播 IP 和 主机名

//int udp_sendiphostname(int udp_port);
  int udp_sendiphostname(int udp_port);



// TCP 监听接受操作指令
int tcp_listenoper(int tcp_port);



int udp_sendiphostname(int udp_port){


     WORD wVersionRequested;
	 WSADATA wsaData;
	 int err;

	 wVersionRequested = MAKEWORD(2,2);

	 err = WSAStartup(wVersionRequested,&wsaData);

	 if(err!=0){
         printf("信息:获取IP 无法打开套接字错误\n");
        return 1;
	 }

	 if(LOBYTE(wsaData.wVersion)!=2 || HIBYTE(wsaData.wVersion)!=2) {

         printf("信息:获取IP DLL 版本不正确，应是 2.2 版本\n");
	       WSACleanup();
		   return 1;
	 }

	 SOCKET sockServer = socket(AF_INET,SOCK_DGRAM,0);

	 SOCKADDR_IN addrServer;

	 addrServer.sin_addr.S_un.S_addr = htonl(INADDR_ANY);

	 addrServer.sin_family=AF_INET;
	 addrServer.sin_port=htons(udp_port);


	 int _ret = bind(sockServer,(SOCKADDR *)&addrServer,sizeof(SOCKADDR));

	 if(_ret!=0){
		 printf("信息:获取IP 绑定端口%d失败\n",udp_port);
		 WSACleanup();
		 return 1;
	 }

	 printf("信息:获取IP 监听端口%d\n",udp_port);


	 SOCKADDR_IN addrClient;

	 int len = sizeof(SOCKADDR);

	 char recvBuf[1024]; // 接收指令

     char hostName[512];  // 主机名
     char hostAddress[512];  // 主机IP
     char msg[1024];          // 消息=主机IP-主机名

	 ::memset(recvBuf,0,sizeof(recvBuf));
	 ::memset(hostName,0,sizeof(hostName));
	 ::memset(hostAddress,0,sizeof(hostAddress));
	 ::memset(msg,0,sizeof(msg));

	 ::printf("信息:获取IP 初始化成功\n");

	 while(true){
		 ::printf("信息:获取IP 进入等待广播消息\n");
          int result = recvfrom(sockServer,recvBuf,sizeof(recvBuf),0,(SOCKADDR *)&addrClient,&len);
		  ::printf("信息:获取IP 接收到广播消息 %d\n",result);
		  if(result==SOCKET_ERROR){
              int errCode=WSAGetLastError();
			  printf("信息:获取IP 失败 错误号:%d\n",errCode);
			//  return 1;
			//  break;
		  }else {
			  // cmd:android->pc:getiphostname
			  ::printf("信息:获取IP 接收到广播消息 %s\n",recvBuf);
			  if(strcmp(recvBuf,"cmd:android->pc:getiphostname")==0){
			     
			     HOSTENT *hostEntry;

	             gethostname(hostName , sizeof(hostName));
                 printf("信息:get hostname: %s\n",hostName);
                 hostEntry = gethostbyname( hostName );
                 //提取IP地址
                 if( hostEntry != 0 ){
                      sprintf_s(hostAddress , "%d.%d.%d.%d" ,(hostEntry->h_addr_list[0][0]&0x00ff),(hostEntry->h_addr_list[0][1]&0x00ff),
                         (hostEntry->h_addr_list[0][2]&0x00ff) ,(hostEntry->h_addr_list[0][3]&0x00ff));
                 }
	             printf("信息:get ip: %s\n",hostAddress);
                 sprintf_s(msg,"%s-%s",hostAddress,hostName);
	             printf("信息:%s\n",msg);
				 ::printf("信息:获取IP 发送IP HOSTNAME %s\n",msg);
                 int iret = sendto(sockServer,msg,sizeof(msg),0,(sockaddr*)&addrClient,sizeof(addrClient));
				 ::printf("信息:获取IP 发送IP HOSTNAME %d\n",iret);
                 if (iret == SOCKET_ERROR) {
       
	                   printf("信息:发送IP失败 错误号:%d\t\n ",WSAGetLastError()); 

                         // ::MessageBox(NULL,_T("Error"),_T("Error"),0);
				 }else {
				       printf("信息:发送IP成功\n");
					   break;
				 }
				 // return 0;
				 // break;
			   }
		  }
	 }

	 closesocket(sockServer);
	 WSACleanup();

	 ::printf("信息:获取IP 退出成功\n");
	 return 0;

}






int _tmain(int argc, _TCHAR* argv[])
{


	int udp_port = 20000;

	int tcp_port = 30000;

    int iret;

    // 打印信息

	printf("***************************************************************************\n");
	printf("****                手机控制电脑 V1.12  作者: liaowufeng               ****\n");
	printf("***************************************************************************\n");
	printf("*  注意:运行时请不要关闭此窗口。                                          *\n");






	if(udp_sendiphostname(udp_port)==0){

	      iret = tcp_listenoper(tcp_port);

		  if(iret!=0){
              ::MessageBox(NULL,_T("程序执行失败，请重新执行"),_T("错误"),0);
			  exit(1);
		      return 1;
		  }
	
	}else {
        ::MessageBox(NULL,_T("程序执行失败，请重新执行"),_T("错误"),0);
		exit(1);
		return 1;

	}
	
	return 0;
}





int tcp_listenoper(int tcp_port) {

 WORD wVersionRequested;
	WSADATA wsaData;

	int err;

    wVersionRequested = MAKEWORD(2,2);

    err = WSAStartup(wVersionRequested,&wsaData);

	if(err!=0){
        printf("信息:接收命令 无法打开套接字错误\n");

		//::MessageBox(NULL,_T("无法打开套接字"),_T("错误"),0);
		return 1;

	}

	if(LOBYTE(wsaData.wVersion)!=2 || HIBYTE(wsaData.wVersion)!=2) {
       printf("信息:接收命令 DLL 版本不正确，应是 2.2 版本\n");

	   // ::MessageBox(NULL,_T("DLL 版本不正确，应是 2.2 版本"),_T("错误"),0);
       WSACleanup();
	   return 1;
	}

	SOCKET sockSrv = socket(AF_INET,SOCK_STREAM,0);

	SOCKADDR_IN addrSrv;


	addrSrv.sin_addr.S_un.S_addr=htonl(INADDR_ANY);

	addrSrv.sin_family=AF_INET;
    
	// 端口监听 
	addrSrv.sin_port=htons(tcp_port);

	int _ret = bind(sockSrv,(SOCKADDR*)&addrSrv,sizeof(SOCKADDR));

    if(_ret!=0){
		 printf("信息:接收命令 绑定端口%d失败\n",tcp_port);
		 WSACleanup();
		 return 1;
	 }

	 printf("信息:接收命令 监听端口%d\n",tcp_port);

	listen(sockSrv,5);

	SOCKADDR_IN addrClient;

	int len = sizeof(SOCKADDR);

	

	while(true) {
        SOCKET sockConn = accept(sockSrv,(SOCKADDR*)&addrClient,&len);


		char recvBuf[1024];

		::memset(recvBuf,0,sizeof(recvBuf));
		
		int len = recv(sockConn,recvBuf,sizeof(recvBuf),0);

        
		printf("信息:接收命令:%s\n",recvBuf);

		if(len>0){
			if(strcmp(recvBuf,"cmd:android->pc:shutdown")==0){
				//::MessageBox(NULL,_T("cmd:android->pc:close"),_T("提示"),0);
			    ShutDown();
			}else if(strcmp(recvBuf,"cmd:android->pc:reboot")==0){
			    //
				//::MessageBox(NULL,_T("cmd:android->pc:reboot"),_T("提示"),0);
				::Reboot();
			}else if(strcmp(recvBuf,"cmd:android->pc:logoff")==0){
			    //
				//::MessageBox(NULL,_T("cmd:android->pc:logoff"),_T("提示"),0);
				::LogOff();

			}
		}else {
			printf("信息:接收命令失败");
          // ::MessageBox(NULL,_T("接收指令失败"),_T("错误"),0);
		}

		//::MessageBox(NULL,*recvBuf,_T("接收数据"),0);
        closesocket(sockConn);
       
	}

	WSACleanup();

     return 0;
}



/*
int udp_sendiphostname(int udp_port){


  WSADATA wsaData;
   WORD wVersion = 0x0202;

   sockaddr_in server_addr,client_addr;

   if(WSAStartup(wVersion,&wsaData)!=0){
	   ::MessageBox(NULL,_T("Error"),_T("Error"),0);
       return 1;
   
   }

   SOCKET server = socket(AF_INET,SOCK_DGRAM,IPPROTO_UDP);

   
   server_addr.sin_addr.S_un.S_addr=INADDR_ANY;

   server_addr.sin_family=AF_INET;


   client_addr.sin_addr.S_un.S_addr=INADDR_BROADCAST;
   client_addr.sin_family=AF_INET;

   client_addr.sin_port=htons(udp_port);

   BOOL bBroadcast;

   setsockopt(server,SOL_SOCKET,SO_BROADCAST,(char *)&bBroadcast,sizeof(bBroadcast));

   char hostName[256];
   char hostAddress[256];
   char msg[512];

   	::memset(hostName,0,sizeof(hostName));

	::memset(hostAddress,0,sizeof(hostAddress));

	::memset(msg,0,sizeof(msg));


   HOSTENT *hostEntry;

   gethostname(hostName , sizeof(hostName));
   printf("get hostname: %s\n",hostName);

    hostEntry = gethostbyname( hostName );
    //提取IP地址
    if( hostEntry != 0 )
    {
	  
       sprintf_s(hostAddress , "%d.%d.%d.%d" ,(hostEntry->h_addr_list[0][0]&0x00ff),(hostEntry->h_addr_list[0][1]&0x00ff),
                   (hostEntry->h_addr_list[0][2]&0x00ff) ,(hostEntry->h_addr_list[0][3]&0x00ff));

      
    }

	printf("get ip: %s\n",hostAddress);

    sprintf_s(msg,"%s-%s",hostAddress,hostName);

	printf("%s\n",msg);



  
   int iret = sendto(server,msg,sizeof(msg),0,(sockaddr*)&client_addr,sizeof(client_addr));

   
   if (iret == SOCKET_ERROR) {
       
	   printf( "Call   sendto   failed.   Error   number:   %d\t\n ",   WSAGetLastError()); 


      // ::MessageBox(NULL,_T("Error"),_T("Error"),0);
   }


   closesocket(server);

   WSACleanup();


	return 0;



}
*/