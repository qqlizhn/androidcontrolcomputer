
#include "stdafx.h"



void tcp_listenoper(int tcp_port) {

 WORD wVersionRequested;
	WSADATA wsaData;

	int err;

    wVersionRequested = MAKEWORD(2,2);

    err = WSAStartup(wVersionRequested,&wsaData);

	if(err!=0){
        printf("信息:<接收命令> 无法打开套接字错误\n");

		//::MessageBox(NULL,_T("无法打开套接字"),_T("错误"),0);
		return ;

	}

	if(LOBYTE(wsaData.wVersion)!=2 || HIBYTE(wsaData.wVersion)!=2) {
       printf("信息:<接收命令> DLL 版本不正确，应是 2.2 版本\n");

	   // ::MessageBox(NULL,_T("DLL 版本不正确，应是 2.2 版本"),_T("错误"),0);
       WSACleanup();
	   return ;
	}

	SOCKET sockSrv = socket(AF_INET,SOCK_STREAM,0);

	SOCKADDR_IN addrSrv;


	addrSrv.sin_addr.S_un.S_addr=htonl(INADDR_ANY);

	addrSrv.sin_family=AF_INET;
    
	// 端口监听 
	addrSrv.sin_port=htons(tcp_port);

	int _ret = bind(sockSrv,(SOCKADDR*)&addrSrv,sizeof(SOCKADDR));



    if(_ret!=0){
		 printf("信息:<接收命令> 绑定端口%d失败\n",tcp_port);
		 WSACleanup();
		 return ;
	 }


	printf("信息:<接收命令> 监听端口%d\n",tcp_port);

	listen(sockSrv,5);

	SOCKADDR_IN addrClient;

	int len = sizeof(SOCKADDR);

   ::printf("信息:<接收命令> 初始化成功\n");

	while(true) {

		 ::printf("信息:<接收命令> 进入等待命令消息\n");

        SOCKET sockConn = accept(sockSrv,(SOCKADDR*)&addrClient,&len);


		char recvBuf[1024];

		::memset(recvBuf,0,sizeof(recvBuf));
		
		int len = recv(sockConn,recvBuf,sizeof(recvBuf),0);

        
		printf("信息:<接收命令> :%s\n",recvBuf);

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
			printf("信息:<接收命令> 失败");
          // ::MessageBox(NULL,_T("接收指令失败"),_T("错误"),0);
		}

		//::MessageBox(NULL,*recvBuf,_T("接收数据"),0);
        closesocket(sockConn);
       
	}

	WSACleanup();

     return ;
}