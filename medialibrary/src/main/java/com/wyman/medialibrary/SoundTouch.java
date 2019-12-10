package com.wyman.medialibrary;

/**
 * SoundTouch 库
 * */
public class SoundTouch {
    static {
        //加载 so
        System.loadLibrary("soundtouch");
    }

    //初始化
    private native  static long nativeInit();
}
