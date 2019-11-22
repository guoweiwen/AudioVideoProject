package com.wyman.cameralibrary.core.orientation

typealias DeviceOrientaion = Orientation
typealias ScreenOrientaion = Orientation

/**
 * 记录屏幕方向枚举类
 * */
sealed class Orientation (
        val degrees : Int
){
    sealed class Vertical(degress: Int) : Orientation(degress){
        //屏幕正向垂直
        object Portrait : Vertical(0)

        //屏幕垂直但反转
        object ReversePortrait : Vertical(180)
    }

    sealed class Horizontal(degress: Int) : Orientation(degress){
        //屏幕横屏
        object Landscape : Horizontal(90)

        //横屏反转
        object ReverseLanscape : Horizontal(270)
    }
}

//Int   内置函数
internal fun Int.toOrientation() : Orientation{
    return when (this) {
        0,360 -> Orientation.Vertical.Portrait
        90 -> Orientation.Vertical.ReversePortrait
        180 -> Orientation.Horizontal.Landscape
        270 -> Orientation.Horizontal.ReverseLanscape
        else -> throw IllegalAccessException("Cannot convert $this to absolute Orientation.")
    }
}

/**
 * 计算预览的方向
 * 返回图像相对于当前设备方向的旋转。
 * @param 显示的方向
 * @param 摄像头传感器的方向
 * @param 是否镜像
 * */
fun computePreviewOrientation(
        screenOrientaion: Orientation,
        cameraOrientation : Orientation,
        cameraIsMirrored : Boolean
) : Orientation {
    val mirroredCameraModifier = if (cameraIsMirrored) -1 else 1
    val rotation = (720
            + mirroredCameraModifier * screenOrientaion.degrees
            - cameraOrientation.degrees
            ) % 360
    return rotation.toOrientation()
}

/**
 * 计算图片旋转的方向
 * @param 设备的方向
 * @param 摄像头传感器方向
 * @param true 为是镜像
 * 返回图像相对于当前设备方向的顺时针旋转。
 * */
internal fun computeImageOrientation(
        deviceOrientation : Orientation,
        cameraOrientation : Orientation,
        cameraIsMirrored : Boolean
) : Orientation {
    val screenOrientationDegrees = deviceOrientation.degrees
    val cameraOrientationDegrees = cameraOrientation.degrees

    val rotation = if (cameraIsMirrored) {
        360 - (cameraOrientationDegrees - screenOrientationDegrees + 360) % 360
    } else {
        360 - (cameraOrientationDegrees + screenOrientationDegrees) % 360
    }
    return rotation.toOrientation()
}

/**
 * @param screenOrientation 显示方向
 * @param cameraOrientation 摄像头方向
 * @param cameraIsMirrored true 镜像 false 非镜像
 *  返回显示方向，用户将在该方向上看到正确旋转的输出相机。
 * */
internal fun computeDisplayOrientation(
        screenOrientation : Orientation,
        cameraOrientation : Orientation,
        cameraIsMirrored : Boolean
) : Orientation {
    val screenOrientationDegrees = screenOrientation.degrees
    val cameraRotationDegrees = cameraOrientation.degrees

    val rotation = if(cameraIsMirrored) {
        (360 - (cameraRotationDegrees + screenOrientationDegrees) % 360) % 360
    } else {
        (cameraRotationDegrees - screenOrientationDegrees + 360) % 360
    }
    return rotation.toOrientation()
}

































