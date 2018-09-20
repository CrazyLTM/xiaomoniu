#include "com_jni_winpcap_WinPcapDll.h"
#include<iostream>
#include<string.h>
#include <pcap.h>
#pragma comment(lib,"wpcap.lib")
#pragma comment(lib,"ws2_32.lib")
#pragma warning(disable:4996)


extern "C"  int pcap_findalldevs_ex(char *source, struct pcap_rmtauth *auth, pcap_if_t **alldevs, char *errbuf);
extern "C"  void	pcap_freealldevs(pcap_if_t *d);

typedef struct {
	char* description;
	char*  name;
	int flag;
}pcap4j;

using namespace std;

pcap_if_t *alldevs;
pcap_if_t *d;
pcap_if_t *device;
int inum;
int i = 0;
pcap_t *adhandle;
char errbuf[PCAP_ERRBUF_SIZE];
pcap4j  pcaptemp;
jobject obj;
struct pcap_pkthdr *header;
const u_char *pkt_data;

//获取设备列表和设备的信息包装jobject中并返回（jobject实为java中list的集合）
JNIEXPORT jobject JNICALL Java_com_jni_winpcap_WinPcapDll_findalldevice
(JNIEnv * env, jobject jobj)
{
	jclass list_jcs = env->FindClass("java/util/ArrayList");
	if (list_jcs == NULL) {
		return NULL;
	}
	//获取ArrayList构造函数id
	jmethodID list_init = env->GetMethodID(list_jcs, "<init>", "()V");
	//创建一个ArrayList对象
	jobject list_obj = env->NewObject(list_jcs, list_init, "");
	//获取ArrayList对象的add()的methodID
	jmethodID list_add = env->GetMethodID(list_jcs, "add","(Ljava/lang/Object;)Z");
	//获取Pcap类
	jclass pcap4j = env->FindClass("com/jni/winpcap/Pacap");

	jmethodID pcap4j_method = env->GetMethodID(pcap4j, "<init>",
		"(ILjava/lang/String;Ljava/lang/String;)V");

	//解决eclipse c&&cpp scanf优先于printf的方法
	setvbuf(stdout, NULL, _IONBF, 0);
	/* 获取本机设备列表 */
	if (pcap_findalldevs_ex(PCAP_SRC_IF_STRING, NULL, &alldevs, errbuf) == -1)
	{
		exit(1);
	}

	/* 打印列表 */
	for (d = alldevs; d; d = d->next)
	{
		pcaptemp.description = d->description;
		pcaptemp.name = d->name;
		pcaptemp.flag = d->flags;
		obj = env->NewObject(pcap4j,
									 pcap4j_method,
									 pcaptemp.flag,
									 env->NewStringUTF(pcaptemp.description),
									 env->NewStringUTF(pcaptemp.name));
		env->CallBooleanMethod(list_obj, list_add, obj);

	}

	if (obj!=NULL){

		pcap_freealldevs(alldevs);
		return list_obj;
	}
	else{

		pcap_freealldevs(alldevs);
		return NULL;
	}

}


JNIEXPORT jint JNICALL Java_com_jni_winpcap_WinPcapDll_BindingAndOpenDevice
(JNIEnv *env, jobject jobj, jstring jstr){
	char*   des;
	des = (char*)env->GetStringUTFChars(jstr, 0);

	if (pcap_findalldevs_ex(PCAP_SRC_IF_STRING, NULL, &alldevs, errbuf) == -1)
	{
		exit(1);
	}
	/* 打印列表 */
	for (d = alldevs; d; d = d->next)
	{
		if (!strcmp(des,d->description)||!strcmp(des,d->name)){
			device = d;
			adhandle = pcap_open(device->name,          // 设备名
				65536,            // 65535保证能捕获到不同数据链路层上的每个数据包的全部内容
				PCAP_OPENFLAG_PROMISCUOUS,    // 混杂模式
				1000,             // 读取超时时间
				NULL,             // 远程机器验证
				errbuf            // 错误缓冲池
				);
			if (adhandle==NULL){
				pcap_freealldevs(alldevs);
				return 0;
			}
			else
			{
				return 1;
			}

		}

	}       
	       
			return 0;

}

JNIEXPORT jstring JNICALL Java_com_jni_winpcap_WinPcapDll_loop
(JNIEnv * env, jobject jobj, jint j, jstring jstr1, jstring jstr2){

	int i = j;
	int rx;
	char*   message;
	message = (char*)env->GetStringUTFChars(jstr2, 0);
	
	while ((rx = pcap_next_ex(adhandle, &header, &pkt_data)>=0)){
		if (rx == 0) { 
			continue; 
		}
		else
		{ 
			if (rx < 0) { 
				return env->NewStringUTF("Data Error");
			}
			else
			{   
				int len = header->len;
				FILE *p=fopen("D:\\data.txt", "w");
				if (p == NULL)
				{
					return 0;
				}
				fseek(p, 0, SEEK_END);
				
				fwrite(pkt_data, len,1, p);
					
				fclose(p);
				char* content = "success";
			    return env->NewStringUTF(content);
			}
		}
	}
	
	
}

