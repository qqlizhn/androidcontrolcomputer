// PCServerConsole.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"












int _tmain(int argc, _TCHAR* argv[])
{


	int udp_port = 20000;

	int tcp_port = 30000;

   // int iret;

    // 打印信息

	printf("***************************************************************************\n");
	printf("****                手机控制电脑 V1.141  作者: liaowufeng              ****\n");
	printf("***************************************************************************\n");
	printf("*  注意:运行时请不要关闭此窗口。                                          *\n");
	printf("*  --------------------------------------------------------------------   *\n");
    printf("*  Version 1.141                                                          *\n");
	printf("*  1 修正 BUG ，分隔符由\"-\" 改为 \"#\" ,                                    *\n");
	printf("	 原因 主机名可以包括\"-\" 字符，但不允许包括\"#\"字符                     *\n");
	printf("*  2 修改android 手机提示时间短一些                                       *\n");
	printf("*  3 修正android 手机横屏时的BUG                                          *\n");
	printf("*  ---------------------------------------------------------------------  *\n");	
    printf("*  Version 1.14                                                           *\n");
	printf("*  1 修正 存在多网卡，多IP 下的BUG                                        *\n");
	printf("*  2 支持控制多台电脑                                                     *\n");
	printf("*  ---------------------------------------------------------------------  *\n");



HANDLE udpThreadHandle,commandThreadHandle;
DWORD udpThreadID=0,commandThreadID=0;

   // 建立广播IP 线程

udpThreadHandle = ::CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)udp_sendiphostname,(LPVOID)udp_port,0,&udpThreadID);

if(udpThreadHandle!=NULL){
	printf("<主程序> 成功建立 获取IP 线程 %d\n",udpThreadID);
	// 建立接收命令操作 线程
    commandThreadHandle = ::CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)tcp_listenoper,(LPVOID)tcp_port,0,&commandThreadID);
	if(commandThreadHandle!=NULL){
	   
	   printf("<主程序> 成功建立 接收命令 线程 %d\n",commandThreadID);
	}else {
       ::MessageBox(NULL,_T("程序执行 建立接收命令线程 失败，请重新执行"),_T("错误"),0);
	   exit(1);
	   return 1;
	}
}else {

    ::MessageBox(NULL,_T("程序执行 建立获取IP线程 失败，请重新执行"),_T("错误"),0);
	exit(1);
	return 1;

}

while(true);

return 0;



/*
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

	*/
}









