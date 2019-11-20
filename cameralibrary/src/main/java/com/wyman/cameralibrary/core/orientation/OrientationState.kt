package com.wyman.cameralibrary.core.orientation

/**
 * 设备旋转的状态
 * */
data class OrientationState(
    //即 Orientation 对象
    val deviceOrientation : DeviceOrientaion,

    //Orientation 对象
    val screenOrientation : ScreenOrientaion
)