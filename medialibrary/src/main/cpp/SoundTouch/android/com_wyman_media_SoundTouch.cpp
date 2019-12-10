//
// Created by guoweiwen on 2019-12-09.
//

//导入Android本身有的头文件
#include <jni.h>
#include <SoundTouch.h>

//使用 soundtouch命名空间 使用其库内的变量就不用 "::"这样了
using namespace soundtouch;

extern "C" JNIEXPORT jlong JNICALL
Java_com_wyman_medialibrary_SoundTouch_nativeInit(JNIEnv *env,jclass thiz){

}
