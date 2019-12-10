package com.wyman.cameralibrary

import android.content.Context
import com.wyman.cameralibrary.core.selector.CameraSelector
import com.wyman.cameralibrary.core.selector.back
import com.wyman.cameralibrary.core.selector.front
import com.wyman.utillibrary.selector.firstAvailable
import com.wyman.cameralibrary.core.concurrent.CameraExecutor
import com.wyman.cameralibrary.core.concurrent.CameraExecutor.Operation
import com.wyman.cameralibrary.core.hardware.*
import com.wyman.cameralibrary.core.hardware.Device
import com.wyman.cameralibrary.core.hardware.Display
import com.wyman.cameralibrary.core.hardware.shutDown
import com.wyman.cameralibrary.core.orientation.OrientationSensor
import com.wyman.cameralibrary.preview.CameraRenderer

/**
 * 摄像头调用类
 * */
class CameraHelper @JvmOverloads constructor(
    context: Context,
    view : CameraRenderer,
    //firstAvailable 找第一个可用的摄像头
    lensPosition : CameraSelector = firstAvailable(//传入 front() 或 back()
            front(),
            back()
    ),
    //默认 摄像头配置类
    cameraConfiguration : CameraConfiguration = CameraConfiguration.default(),
    private val executor : CameraExecutor = EXECUTOR//传了默认值进去
){

    //设备显示情况
    private val display = Display(context)

    private val device = Device(
        cameraRenderer = view,
        display = display,
        initiaLensPositionSelector = lensPosition,
        initialConfiguration = cameraConfiguration,
        executor = executor
    )

    /**
     * 监听设备角度类
     * */
    private val orientationSensor = OrientationSensor(
            context = context,
            device = device
    )

    /**
     * 暴露外部使用
     * 开启摄像头
     * */
    fun start(){
        executor.execute(Operation{
            //执行的任务
            device.bootStart(
                    orientationSensor = orientationSensor
            )
        })
    }

    /**
     * 暴露外部使用
     * 停止摄像头
     * */
    fun stop(){
        //取消所有任务，未执行完等待执行完就不再执行
        executor.cancelTasks()
        executor.execute(Operation{
            //执行的任务
            device.shutDown(
                    orientationSensor = orientationSensor
            )
        })
    }


    companion object{
        private val EXECUTOR = CameraExecutor()
    }

}