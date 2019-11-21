package com.wyman.cameralibrary.core.characteristic

import android.hardware.Camera
import com.wyman.cameralibrary.core.orientation.Orientation
import com.wyman.cameralibrary.core.orientation.toOrientation

/***
 * 封装 摄像头 信息
 * Characteristics 包含 LensPosition
 */
internal data class Characteristics (
        val cameraId : Int,
        //记录前置还是后置摄像头
        val lensPosition : LensPosition,
        //摄像头的方向
        val cameraOrientation : Orientation,
        //是否镜像
        val isMirrored : Boolean
)

/**
 * 传入 camerId 获取封装摄像头信息
 * */
internal fun getCharacteristics(cameraId : Int) : Characteristics{
    val info = Camera.CameraInfo()
    Camera.getCameraInfo(cameraId,info)
    //info.facing 为正在打开是前置摄像头还是后置摄像头 再调用 toLensPosition() 返回自己对应前置或后置摄像头的参数
    val lensPosition = info.facing.toLensPosition()
    return Characteristics(
            cameraId = cameraId,
            lensPosition = lensPosition,
            cameraOrientation = info.orientation.toOrientation(),
            isMirrored = lensPosition == LensPosition.Front
    )
}
