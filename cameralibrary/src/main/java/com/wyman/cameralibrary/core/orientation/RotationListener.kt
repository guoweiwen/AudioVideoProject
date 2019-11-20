package com.wyman.cameralibrary.core.orientation

import android.content.Context
import android.view.OrientationEventListener

/**
 * 原生 OrientationEventListener 的加强版
 * */
internal open class RotationListener(
        context: Context
) : OrientationEventListener(context){

    //回调函数
    lateinit var orientationChanged : (DeviceRotationDegrees) -> Unit

    override fun onOrientationChanged(orientation: Int) {
        //重写 OrientationEventListener 的该方法
        if(canDetectOrientation()/*检查是否能旋转*/){
            //能旋转 回调方法出去
            orientationChanged(orientation)
        }
    }
}