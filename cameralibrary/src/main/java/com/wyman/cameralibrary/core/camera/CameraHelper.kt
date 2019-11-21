package com.wyman.cameralibrary.core.camera

import android.content.Context
import com.wyman.cameralibrary.core.selector.CameraSelector
import com.wyman.cameralibrary.core.selector.back
import com.wyman.cameralibrary.core.selector.front
import com.wyman.utillibrary.selector.firstAvailable
import com.wyman.cameralibrary.core.concurrent.CameraExecutor
import com.wyman.cameralibrary.core.concurrent.CameraExecutor.Operation
import com.wyman.cameralibrary.core.hardware.Device
import com.wyman.cameralibrary.core.hardware.Display
import com.wyman.cameralibrary.core.hardware.shutDown
import com.wyman.cameralibrary.core.orientation.OrientationSensor

/**
 * 摄像头调用类
 * */
class CameraHelper @JvmOverloads constructor(
    context: Context,
    cameraType : CameraSelector = firstAvailable(//传入 front() 或 back()
            front(),
            back()
    ),

    private val executor : CameraExecutor = EXECUTOR
){

    private val display = Display(context)

    private val device = Device(
        display = display
    )

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
            //执行任务

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
            //
            device.shutDown(
                    orientationSensor = orientationSensor
            )
        })
    }


    companion object{
        private val EXECUTOR = CameraExecutor()
    }

}