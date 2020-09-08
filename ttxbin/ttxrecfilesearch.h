#ifndef _TTX_REC_FILE_SEARCH_H_
#define _TTX_REC_FILE_SEARCH_H_

/*
 *1、 如果帧缓存里面有数据，则打开录像对象进行录像， 一段时间没数据就把录像给停止了
 *2、录像上报时，把当前录像停止了，判断开始录像的时间有没有超过5秒，
 *      如果超过了就主动停止录像，再打开一个文件
 *3、录像切换，写I帧时判断录像时长是否超过30分钟了，如果是，则重启录像
*/


//#include "ttxpublic.h"
#include "ttxdvrnetdef.h"


//获取录像文件信息
int ttx_search_file(int nYear, int nMonth, int nDay, int nBeginTime, int nEndTime, int nRecType, int nChannel
				, TTXPfnRecFindCB pfnRecFindCB, void* pFindUsr, TTXPfnGetExitCB pfnGetExitCB, void* pExitUsr);	


#endif

