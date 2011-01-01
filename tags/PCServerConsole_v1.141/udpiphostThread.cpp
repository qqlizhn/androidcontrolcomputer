
#include "stdafx.h"



void udp_sendiphostname(int udp_port){


     WORD wVersionRequested;
	 WSADATA wsaData;
	 int err;

	 wVersionRequested = MAKEWORD(2,2);

	 err = WSAStartup(wVersionRequested,&wsaData);

	 if(err!=0){
         printf("信息:<获取IP> 无法打开套接字错误\n");
        return ;
	 }

	 if(LOBYTE(wsaData.wVersion)!=2 || HIBYTE(wsaData.wVersion)!=2) {

         printf("信息:<获取IP> DLL 版本不正确，应是 2.2 版本\n");
	     WSACleanup();
		 return ;
	 }

	 SOCKET sockServer = socket(AF_INET,SOCK_DGRAM,0);

	 SOCKADDR_IN addrServer;

	 addrServer.sin_addr.S_un.S_addr = htonl(INADDR_ANY);

	 addrServer.sin_family=AF_INET;
	 addrServer.sin_port=htons(udp_port);


	 int _ret = bind(sockServer,(SOCKADDR *)&addrServer,sizeof(SOCKADDR));

	 if(_ret!=0){
		 printf("信息:<获取IP> 绑定端口%d失败\n",udp_port);
		 WSACleanup();
		 return ;
	 }

	 printf("信息:<获取IP> 监听端口%d\n",udp_port);


	 SOCKADDR_IN addrClient;

	 int len = sizeof(SOCKADDR);

	 char recvBuf[1024]; // 接收指令

     char hostName[512];  // 主机名
     char hostAddress[512];  // 主机IP
     char msg[1024];          // 消息=主机IP-主机IP-主机IP-主机名



	 ::printf("信息:<获取IP> 初始化成功\n");

	 while(true){

         ::memset(recvBuf,0,sizeof(recvBuf));
	     ::memset(hostName,0,sizeof(hostName));
	     ::memset(hostAddress,0,sizeof(hostAddress));
	     ::memset(msg,0,sizeof(msg));

		 ::printf("信息:<获取IP> 进入等待广播消息\n");
          int result = recvfrom(sockServer,recvBuf,sizeof(recvBuf),0,(SOCKADDR *)&addrClient,&len);
		  ::printf("信息:<获取IP> 接收到广播消息 %d\n",result);
		  if(result==SOCKET_ERROR){
              int errCode=WSAGetLastError();
			  printf("信息:<获取IP> 失败 错误号:%d\n",errCode);
			//  return 1;
			//  break;
		  }else {
			  // cmd:android->pc:getiphostname
			  ::printf("信息:<获取IP> 接收到广播消息 %s\n",recvBuf);
			  if(strcmp(recvBuf,"cmd:android->pc:getiphostname")==0){
			     
			     HOSTENT *hostEntry;

	             gethostname(hostName , sizeof(hostName));
                 printf("信息:<获取IP> get hostname: %s\n",hostName);
                 hostEntry = gethostbyname( hostName );
                 //提取IP地址
                 if( hostEntry != 0 ){
                     // 解决存在多IP 下的 BUG
					 for(int _i=0;hostEntry->h_addr_list[_i]!=0;_i++){
						 char ip[32];
						 // 修正 BUG ，分隔符由"-" 改为 "#" ,原因 主机名可以包括"-" 字符，但不允许包括"#"字符
						 sprintf_s(ip,"%d.%d.%d.%d#",hostEntry->h_addr_list[_i][0]&0x00ff,
							 hostEntry->h_addr_list[_i][1]&0x00ff,hostEntry->h_addr_list[_i][2]&0x00ff,hostEntry->h_addr_list[_i][3]&0x00ff);
						 strcat_s(hostAddress,ip);
					 }
                 }
	             printf("信息:<获取IP> get ip: %s\n",hostAddress);
                 sprintf_s(msg,"%s%s",hostAddress,hostName);
	             printf("信息:<获取IP> %s\n",msg);
				 ::printf("信息:<获取IP> 发送IP HOSTNAME %s\n",msg);
                 int iret = sendto(sockServer,msg,sizeof(msg),0,(sockaddr*)&addrClient,sizeof(addrClient));
				 ::printf("信息:<获取IP> 发送IP HOSTNAME %d\n",iret);
                 if (iret == SOCKET_ERROR) {
       
	                   printf("信息:<获取IP> 发送IP失败 错误号:%d\t\n ",WSAGetLastError()); 

                         // ::MessageBox(NULL,_T("Error"),_T("Error"),0);
				 }else {
				       printf("信息:<获取IP> 发送IP成功\n");
					  // break;
				 }
				 // return 0;
				 // break;
			   }
		  }
	 }

	 closesocket(sockServer);
	 WSACleanup();
	 ::printf("信息:<获取IP> 退出成功\n");
	 return ;

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