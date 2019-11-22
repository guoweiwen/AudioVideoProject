package com.wyman.cameralibrary.core.characteristic

import android.hardware.Camera
import java.lang.IllegalArgumentException


//LensPositionSelector 为 Iterable<LensPosition>的别名
typealias LensPositionSelector = Iterable<LensPosition>.() -> LensPosition?

/**
 * Created by wyman
 * on 2019-09-13.
 * sealed 是密封类 即 enum 枚举类
 * LensPosition 指透视镜头
 */
sealed class LensPosition {

    //后置摄像头
    object Back : LensPosition(){
        override fun toString(): String = "LensPosition.Back"
    }

    //前置摄像头
    object Front : LensPosition(){
        override fun toString(): String = "LensPosition.Front"
    }

    //额外摄像头
    object External : LensPosition(){
        override fun toString(): String = "LensPosition.External"
    }
}

/**
 * 通过此方法获得摄像头 id
 * */
internal fun Int.toLensPosition() : LensPosition =
        when (this) {//this 为 Int
            Camera.CameraInfo.CAMERA_FACING_FRONT -> LensPosition.Front
            Camera.CameraInfo.CAMERA_FACING_BACK -> LensPosition.Back
            else -> throw  IllegalArgumentException("Lens position $this is not supported ")
        }

/**
 *  获取摄像头
 *  先获得摄像头Id 再调用上面的 toLensPosition 获得LensPosition
 */
fun LensPosition.toCameraId() : Int =
        (0 until Camera.getNumberOfCameras())
                .find { cameraId ->
                    this == getCharacteristics(cameraId).lensPosition
                    // ?: 为 Elvis 操作符 找不到设备即抛出异常找到就返回 Int类型
                } ?: throw IllegalArgumentException("Device has no camera for the desired lens position(s).")
        //找不到抛出异常说设备找不到摄像头


















