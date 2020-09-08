#include <stdio.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/time.h>
#include <dirent.h>
#include <string.h>
#include <time.h>

#include "ttxrecfilesearch.h"
//#include "ttxlogger.h"

static const char * const STRINGS[] = {
    "/storage/sdcard0/dvr/",
    "/storage/sdcard1/dvr/",
    "/storage/usbotg/dvr/",
    "/storage/usbotg1/dvr/",
    "/storage/usbotg2/dvr/",
    "/storage/usbotg3/dvr/"
};

static const char * const _CHANNEL[] = {
    "front/",
    "back/",
    "left/",
    "right/"
};

#define _CHANNEL_COUNT 4
#define _STRINGS_COUNT 6


const char* CheckFactoryRecordName(const char* szFactoryRecFile)
{
    const char *delims="_";
    int cnt =0;
	const char* p = strrchr(szFactoryRecFile, '/');
	if(NULL == p)
	{
		p = strrchr(szFactoryRecFile, '\\');
	}
	
	if (p)
	{
		p = p + 1;
	}

    char *buffer = strdup(p);

    buffer = strtok(buffer, delims);

    while(buffer != NULL){
        //printf("word: %s\n",buffer);
        cnt ++;
        buffer = strtok(NULL,delims);
    }

  if(cnt < 3)
	{
		return NULL;
	}
	else
	{
		return p;
	}
}


//word: quart
//  word: 720P
//  word: 2020-09-04
void getRecodeYearMonthDay(char* time, TTXRecFile_S& RFile){
    int  i= 0;
    int nTemp = 0;
    char *buffer = strdup(time);
    buffer = strtok(buffer, "-");
    while(buffer != NULL){
        if(i == 0){
    		sscanf( buffer, "%4d", &nTemp);
    		RFile.Year = nTemp - 2000;
    	}else if( i == 1){
    		sscanf( buffer, "%2d", &nTemp);
    		RFile.Month = nTemp;
        }else if( i  == 2 ){
            sscanf( buffer, "%2d", &nTemp);
            RFile.Day = nTemp;
        }
        i++;
        buffer = strtok(NULL,"-");
    }
    free(buffer);
    //printf(" getRecodeYearMonthDay :%d : %d: %d \n",RFile.Year,RFile.Month,RFile.Day);
}

//  word: 15-31-45.mp4
void getRecodeTime(char* time, TTXRecFile_S& RFile){
    int  i= 0;
    int nTemp = 0;
    char *buffer = strdup(time);
    buffer = strtok(buffer, "-");
    while(buffer != NULL){
        if(i == 0){
    		sscanf(buffer, "%2d", &nTemp);
    		RFile.Hour = nTemp;
    	}else if( i == 1){
    		sscanf( buffer, "%2d", &nTemp);
    		RFile.Minute = nTemp;
        }else if( i  == 2 ){
            sscanf( buffer, "%2d", &nTemp);
            RFile.Second = nTemp;
        }
        i++;
        buffer = strtok(NULL,"-");
    }
   free(buffer);
   //printf(" getRecodeTime :%d : %d: %d \n",RFile.Hour,RFile.Minute,RFile.Second);
}

	
bool GetFactoryRecInfo(const char* szFactoryRecFile, TTXRecFile_S& RFile)
{
    int cnt = 0;
    const char *delims="_";
	const char* p = strrchr(szFactoryRecFile, '/');

	 char *yearMouthDay  ;
	 char *time;

	if (p){
		p = p + 1;
	}

	if (p){

        char *buffer = strdup(p);
        buffer = strtok(buffer, delims);
        while(buffer != NULL){
            //printf("word: %d = %s\n",cnt,buffer);
            if(cnt == 2){
                yearMouthDay =  strdup(buffer);
            }else if(cnt == 3){
                time =  strdup(buffer);
            }else if(cnt == 4){

            }
            cnt++;
            buffer = strtok(NULL,delims);
        }

        getRecodeYearMonthDay(yearMouthDay,RFile);
        free(yearMouthDay);

        getRecodeTime(time,RFile);
        free(time);

		RFile.uiFileTime = 3 * 60;
		
		return true;
	}
	else
	{
		return false;
	}
}

int ttx_search_file(int nYear, int nMonth, int nDay, int nBeginTime, int nEndTime, int nRecType, int nChannel
				, TTXPfnRecFindCB pfnRecFindCB, void* pFindUsr, TTXPfnGetExitCB pfnGetExitCB, void* pExitUsr)
{
	#if 1

	char szDir[256] = {0};

    for (int32_t i = 0; i< _STRINGS_COUNT; ++i) {
       for (int32_t j = 0; j< _CHANNEL_COUNT; ++j) {

            if (j == nChannel
                   || nChannel == TTX_CHANNEL_ALL
                   || nChannel == TTX_CHANNEL_ALL_EX ){


                memset(szDir, '\0', strlen(szDir) );
                strncpy(szDir, STRINGS[i], strlen(STRINGS[i]));
                strcat(szDir, _CHANNEL[j]);

                printf("CTTXNetService::szDir = %s \n", szDir);

                if (TTX_RECTYPE_ALL == nRecType || TTX_RECTYPE_NORMAL == nRecType)
                {
                    //Ŀ¼
                    DIR * dir = NULL;
                    //Ŀ¼����
                    struct dirent *dir_env;
                    //�ļ�����
                    struct stat stat_file;
                    //��Ŀ¼
                    dir = opendir(szDir);
                    if (dir != NULL)
                    {
                        while( (dir_env = readdir(dir)) != NULL )//���ļ�������
                        {
                            //�ж��Ƿ���Ҫ�˳�
                            if (pfnGetExitCB(pExitUsr))
                            {
                                printf("CTTXRecFileSearch::ttx_search_file pfnGetExitCB true");
                                break;
                            }

                            //����CPUռ��
                            //ttxSleep(0);
                             sleep(1);
                            //�ų�.��..
                            if(strcmp(dir_env->d_name,".")==0  || strcmp(dir_env->d_name,"..")==0)
                            {
                                continue;
                            }

                            TTXRecFile_S RFile;
                            memset(&RFile, 0, sizeof(RFile));
                            //���ļ�ȫ�����浽�±���
                            strcpy(RFile.szFileName, szDir);
                            strcat(RFile.szFileName, "/");
                            strcat(RFile.szFileName, dir_env->d_name);
                            const char* pRecType = RFile.szFileName + strlen(RFile.szFileName) - 4;

                            if(0 == strcmp(pRecType, ".mp4"))
                            {
                                printf("CTTXRecFileSearch:: is mp4 @@szFileName %s \n", RFile.szFileName);

                            }else{
                                printf("CTTXNetService:: format not support  szFileName %s \n", RFile.szFileName);
                                continue;
                            }

                            //��ȡ�ļ�������Ϣ
                            int ret = stat(RFile.szFileName, &stat_file);
                            if (ret >= 0)
                            {
                                if( !S_ISDIR(stat_file.st_mode))
                                {
                                    RFile.uiFileLen = stat_file.st_size;


                                    int recBegTime = 0;
                                    int recEndTime = 0;
                                   // printf("CTTXRecFileSearch::ttx_search_file  readdir:%s \n",RFile.szFileName);
                                   // printf("CTTXRecFileSearch::ttx_search_file  uiFileLen :%d \n",RFile.uiFileLen);

                                    if(CheckFactoryRecordName(RFile.szFileName))
                                    {
                                        GetFactoryRecInfo(RFile.szFileName, RFile);
                                        ///FInfo.nTotalMinSecond = RFile.uiFileTime;
                                        //FInfo.nChannel = RFile.Channel;
                                        RFile.Channel = j;
                                        recBegTime = RFile.Hour * 3600 + RFile.Minute * 60 + RFile.Second;
                                        recEndTime = recBegTime + RFile.uiFileTime;

                                        printf("CTTXRecFileSearch:: readdir %s, %d,%d,%d %d:%d:%d nTotalMinSecond:%d channle:%d %d:%d:%d \n",
                                        RFile.szFileName, RFile.Year, RFile.Month, RFile.Day,RFile.Hour,RFile.Minute,RFile.Second,
                                        RFile.uiFileTime,RFile.Channel, nYear, nMonth, nDay);
                                        if (recEndTime > 86400)
                                        {
                                            recEndTime = 86400;
                                        }
                                        if (RFile.Year  == (nYear - 2000) && RFile.Month == nMonth && RFile.Day == nDay
                                         && ( ( recBegTime >= nBeginTime && recBegTime <= nEndTime)
                                            || ( recEndTime >= nBeginTime && recEndTime <= nEndTime )
                                            || ( recBegTime <= nBeginTime && recEndTime >= nEndTime ) ))
                                        {
                                            //�Ƿ�����¼��״̬��ȷ��
                                            printf("CTTXRecFileSearch  find one file  %d:%d:%d \n", nYear, nMonth, nDay);
                                            RFile.bStream = false;
                                            RFile.Type = 0;
                                            pfnRecFindCB(&RFile, pFindUsr);
                                        }
                                    }

                                }
                            }
                        }
                        closedir(dir);	dir = NULL;
                    }
                }
             }
        }
	}
	#endif
	return 0;

}



	
