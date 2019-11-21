package com.wyman.cameralibrary.core.orientation

import android.content.Context
import com.wyman.cameralibrary.core.hardware.Device

/**
 * 设备旋转角度监视器
 * */
internal open class OrientationSensor(/*入口*/
        private val rotationListener: RotationListener,
        private val device : com.wyman.cameralibrary.core.hardware.Device
) {
    //DeviceRotationDegrees 需要传入
    private val onOrientationChanged : (DeviceRotationDegrees) -> Unit = { deviceRotation ->//OdeviceRotation 为 DeviceRotationDegrees
        deviceRotation.toCloseestRightAngle()
                .toOrientation()
                .takeIf { it != lastKnownDeviceOrientation }//如果不是上次的设备的角度
                ?.let{
                      val state = OrientationState(
                            deviceOrientation = it,
                            screenOrientation = device.getScreenOrientation()
                      )
                        //记录设备旋转角度
                      lastKnownDeviceOrientation = state.deviceOrientation
                    //回调函数
                    listener(state)
                }

    }

    //默认是竖屏
    private var lastKnownDeviceOrientation : Orientation = Orientation.Vertical.Portrait

    //OrientationState 接口
    private lateinit var listener : (OrientationState) -> Unit

    /**
     * 该构造函数给外部使用
     * 自己再调用 this() 构造函数
     * */
    constructor(
            context : Context,
            device : Device
    ) : this (
            RotationListener(context),
            device
    )

    init {
        rotationListener.orientationChanged = onOrientationChanged
    }

    /**
     * 开始监听设备方向
     * */
    open fun start(listener : (OrientationState) -> Unit) {
        this.listener = listener
        //该方法是 OrientationEventListener
        rotationListener.enable()
    }

    /**
     * 停止监听设备方向
     * */
    open fun stop(){
        rotationListener.disable()
    }
}