package com.wyman.cameralibrary.core.characteristic

/**
 * Created by wyman
 * on 2019-09-13.
 * sealed 是密封类 即 enum 枚举类
 * LensPosition 指透视镜头
 */
sealed class LensPosition {

    //后置摄像头
    object Back : com.wyman.cameralibrary.core.characteristic.LensPosition(){
        override fun toString(): String = "LensPosition.Back"
    }

    //前置摄像头
    object Front : com.wyman.cameralibrary.core.characteristic.LensPosition(){
        override fun toString(): String = "LensPosition.Front"
    }

    //额外摄像头
    object External : com.wyman.cameralibrary.core.characteristic.LensPosition(){
        override fun toString(): String = "LensPosition.External"
    }
}

















