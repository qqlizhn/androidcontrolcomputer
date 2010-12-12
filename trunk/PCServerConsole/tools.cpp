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