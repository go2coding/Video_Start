/*************************************************************************
    > File Name: jni_mad_mp3_wrap.cpp
    > Author: zhongjihao
    > Mail: zhongjihao100@163.com
    > Created Time: 2015年07月03日 星期五 20时07分54秒
 ************************************************************************/
//#include "../baseclass/jni_register.h"
#include <jni.h>
#include <string.h>
#include <android/log.h>

#define LOG_TAG "C_TAG"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

extern "C"
{
    #include "NativeMP3Decoder.c"
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tb_mp3wave_NativeMP3Decoder_initAudioPlayer(JNIEnv *env, jobject obj, jstring file,jint startAddr)
{
	const char* fileString = env->GetStringUTFChars(file, NULL);
    int ret = NativeMP3Decoder_init(fileString,startAddr);
	if(ret == -1)
	{
		LOGE("=======JNI===initAudioPlayer===error===");
		env ->ReleaseStringUTFChars(file,fileString);
		//ThrowErrnoException(env, "java/io/IOException", errno);
		env->ThrowNew(env->FindClass("java/io/IOException"), "this is IllegalArgumentException error form C++, because the str length is 5");
		return (jint)ret;
	}
	LOGI("=======JNI===initAudioPlayer===success===");
	env ->ReleaseStringUTFChars(file,fileString);
	return (jint)ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tb_mp3wave_NativeMP3Decoder_getAudioBuf(JNIEnv *env, jobject obj ,jshortArray audioBuf,jint len)
{
	int bufsize = 0;
	int ret = 0;
    if(audioBuf != NULL)
	{
		bufsize = env->GetArrayLength(audioBuf);
        jshort *_buf = env->GetShortArrayElements(audioBuf,NULL);
		memset(_buf, 0, bufsize*2);
		ret = NativeMP3Decoder_readSamples(_buf, len);
		env->ReleaseShortArrayElements(audioBuf,_buf,0);
	}
	else
	{
		LOGE("====JNI===传入的数组为空====");
	}
	return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tb_mp3wave_NativeMP3Decoder_getAudioSamplerate(JNIEnv *env,jobject obj)
{
	return NativeMP3Decoder_getAudioSamplerate();
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tb_mp3wave_NativeMP3Decoder_getAudioFileSize(JNIEnv *env,jobject obj)
{
	return (jint)getAudioFileSize();
}

extern "C" JNIEXPORT void JNICALL
Java_com_tb_mp3wave_NativeMP3Decoder_rePlayAudioFile(JNIEnv *env,jobject obj)
{
	rePlayAudioFile();
}

extern "C" JNIEXPORT void JNICALL
Java_com_tb_mp3wave_NativeMP3Decoder_closeAudioFile(JNIEnv *env,jobject obj)
{
	NativeMP3Decoder_closeAudioFile();
}

