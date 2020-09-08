#ifndef _TTX_DVR_NET_DEF_H_
#define _TTX_DVR_NET_DEF_H_

//¼��ͨ��
#define TTX_CHANNEL_ALL			98		//����ͨ��
#define TTX_CHANNEL_ALL_EX		-1		//����ͨ��


#define TTX_CHANNEL_FRONT			0
#define TTX_CHANNEL_BACK			1
#define TTX_CHANNEL_LEFT			2
#define TTX_CHANNEL_RIGHT			3

//¼�����Ͷ���
#define TTX_RECTYPE_NORMAL		1		//����¼��
#define TTX_RECTYPE_ALARM		2		//����¼��
#define TTX_RECTYPE_ALL			3		//����¼��

#ifdef WIN32
#define I64 __int64	
#define U64 unsigned __int64 
#define _u64long unsigned __int64
#else
#define I64 long long
#define U64 unsigned long long
#define _u64long unsigned long long
#endif


typedef struct _tagTTXRecFile
{
	unsigned char Year;		//�꣬11 = 2011��
	unsigned char Month;	//�գ���1��ʼ��1 - 12
	unsigned char Day;		//�죬��1��ʼ��1 - 31
	unsigned char Hour;		//Сʱ����0��ʼ��0 - 23
	unsigned char Minute;	//���ӣ���0��ʼ��0 - 59
	unsigned char Second;	//�룬��0��ʼ��0 - 59
	unsigned char Type;		//�ļ�����		TTX_RECTYPE_NORMAL, TTX_RECTYPE_ALARM, TTX_RECTYPE_ALL
	unsigned char Channel;	//ͨ���ţ���0��ʼ
	unsigned int  uiFileTime;	//�ļ�ʱ������Ϊ��λ
	unsigned int  uiFileLen;	//�ļ�����
	char	szFileName[244];
	char	cYJWNewFlag;		//���ļ�ʹ�ã�����0
	char	cCoointNewFlag;		//���ļ�ʹ�ã�����0
	char	bRecording;			//�Ƿ�����¼��0��ʾû�У�1��ʾ����¼��
	char	bStream;				//�Ƿ�Ϊ��ʽ�ļ�����Ϊ��ʽ�ļ�ʱ���ͻ��˻�ʹ�ûطŵķ�ʽ�����ļ�����
	unsigned int ChnMask;		//ͨ�����룬��һ���ļ����ڶ��ͨ����¼��ʱ��ʹ��Щ����
	int 	nAlarmInfo;			//һ���ļ������ж�����������ļ��Ǳ����ļ�ʱ��Ч
}TTXRecFile_S, *LPTTXRecFile_S;

//�ļ����һص�����
//@pFile		¼���ļ�
//@pUsr		�û��Զ�������
typedef void(* TTXPfnRecFindCB)(LPTTXRecFile_S pFile, void* pUsr);

//�ж��Ƿ���Ҫ�˳�
//����true��ʾ�˳�
//�ļ����ҹ����У���Ҫ��ͣ���ô˺������ж��Ƿ���Ҫ�˳�
typedef bool (* TTXPfnGetExitCB)(void* pUsr);






#endif

