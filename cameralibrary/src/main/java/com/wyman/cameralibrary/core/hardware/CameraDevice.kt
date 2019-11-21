package com.wyman.cameralibrary.core.hardware

import android.hardware.Camera
import android.view.Surface
import com.wyman.cameralibrary.core.characteristic.Characteristics
import com.wyman.cameralibrary.core.characteristic.toCameraId
import com.wyman.cameralibrary.core.orientation.Orientation
import com.wyman.cameralibrary.core.orientation.Orientation.Vertical.Portrait
import kotlinx.coroutines.CompletableDeferred
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

internal open class CameraDevice (
        val characteristics: Characteristics
){
    private lateinit var camera : Camera
    private lateinit var surface : Surface
    //图片的方向
    private var imageOrientation : Orientation = Portrait
    //预览方向
    private var previewOrientation : Orientation = Portrait

    private val capabilities = CompletableDeferred<Capabilities>()

    /**
     * 打开摄像头
     * */
    open fun open(){
        val lensPosition = characteristics.lensPosition
        val cameraId = lensPosition.toCameraId()

        try{
            camera = Camera.open(cameraId)
            capabilities.complete(camera.getCapabilities())
            //previewStream = PreviewStream(camera)
        } catch (e : RuntimeException){
            throw IllegalArgumentException("Failed to open camera with lens position: $lensPosition and id: $cameraId")
        }
    }

    /**
     * 关闭摄像头
     * */
    open fun close(){
        surface.release()
        camera.release()
    }

    /**
     * 开启预览
     * */
    open fun startPreview(){
        try{
            camera.startPreview()
        } catch (e : RuntimeException){
            throw IllegalArgumentException("Failed to start preview for camera with lens " +
                    " position: ${characteristics.lensPosition} and id: ${characteristics.cameraId}")
        }
    }

    /**
     * 关闭预览
     * */
    open fun stopPreview(){
        camera.stopPreview()
    }
}