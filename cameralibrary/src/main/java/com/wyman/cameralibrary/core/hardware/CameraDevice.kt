package com.wyman.cameralibrary.core.hardware

import android.hardware.Camera
import android.view.Surface
import com.wyman.cameralibrary.core.characteristic.Characteristics
import com.wyman.cameralibrary.core.characteristic.toCameraId
import com.wyman.cameralibrary.core.orientation.*
import com.wyman.cameralibrary.core.orientation.Orientation.Vertical.Portrait
import com.wyman.cameralibrary.core.orientation.computeDisplayOrientation
import com.wyman.cameralibrary.core.orientation.computeImageOrientation
import com.wyman.cameralibrary.preview.Preview
import com.wyman.cameralibrary.preview.PreviewStream
import com.wyman.cameralibrary.result.Photo
import kotlinx.coroutines.CompletableDeferred
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

typealias PreviewSize = Resolution

internal open class CameraDevice (
        val characteristics: Characteristics
){
    private lateinit var camera : Camera
    private lateinit var surface : Surface
    private lateinit var previewStream : PreviewStream
    //图片的方向
    private var imageOrientation : Orientation = Portrait
    //预览方向
    private var previewOrientation : Orientation = Portrait
    //显示器方向
    private var displayOrientation : Orientation = Portrait

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
            previewStream = PreviewStream(camera)
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

    /**
     * 返回预览分辨率
     * */
    open fun getPreviewResolution() : Resolution{
        return camera.getPreviewResolution(previewOrientation)
    }

    /**
     * 拍照
     * 这里用到了一线程等待另一线程执行完再执行
     * CountDownLatch jdk concurrent 并发包
     * */
    private fun Camera.takePhoto(imageRotation : Int) : Photo{
        val latch = CountDownLatch(1)//1 为被调用的次数
        //用于封装 Photo
        val photoReference = AtomicReference<Photo>()

        //Camera 系统类的方法
        takePicture(
                null,
                null,
                null,
                Camera.PictureCallback { data, camera ->
                    //回调函数将 Photo 封装
                    photoReference.set(
                            Photo(data,imageRotation)
                    )
                    latch.countDown()//唤醒主线程
                }
        )
        //等待 takePicture 回调完后再往下执行
        latch.await()
        return photoReference.get()
    }

    private fun Camera.Parameters.setInCamera() = apply {
        camera.parameters = this//this 为 Camera.Parameters非当前类
    }

    /**
     * 设置摄像头预览的 view
     * @param preview 为 data class
     * kotlin 双引号是把一个方法当做一个参数，传递到另一个方法中进行使用，通俗的来讲就是引用一个方法
     * */
    @Throws(IOException::class)
    private fun Camera.setDisplaySurface(preview : Preview) : Surface = when (preview){
        /*
        * 将 preview.surfaceTexture 的 surfaceTexture 放进 Camera的setPreviewTexture 的 SurfaceTexture
        * 再返回 surface
        * */
        is Preview.Texture -> preview.surfaceTexture
                .also (this::setPreviewTexture) //also 设置完对象不变
                //let 操作完对象发生变化
                .let(::Surface) //将调用 Surface(SurfaceTexture surfaceTexture) 将 preview.surfaceTexture构建一个Surface

        /*
        * 将 preview.surfaceHolder 的 surfaceHolder 放进 Camera的setPreviewDisplay 的 SurfaceHolder
        * 再返回 surface
        * */
        is Preview.Surface -> preview.surfaceHolder
                .also (this::setPreviewDisplay)//为 Camera
                .surface
    }

    /**
     * 获取设备摄像头宽高
     * */
    private fun Camera.getPreviewResolution(previewOrientation : Orientation) : Resolution{
        return parameters.previewSize
                .run {//返回设置后的对象
                    PreviewSize(width,height)//width height 为 Camera的宽高
                }
                .run{
                    when (previewOrientation){
                        is Orientation.Vertical -> this
                        //横屏为高为宽；宽为高的分辨率
                        is Orientation.Horizontal -> flipDimensions() //即 Resolution
                    }
                }
    }

    /**
     * 设置当前显示屏幕的方向
     * */
    open fun setDisplayOrientation(orientationState : OrientationState){
        imageOrientation = computeImageOrientation(
            deviceOrientation = orientationState.deviceOrientation,
            cameraOrientation = characteristics.cameraOrientation,
            cameraIsMirrored = characteristics.isMirrored
        )

        displayOrientation = computeDisplayOrientation(
            screenOrientation = orientationState.screenOrientation,
            cameraOrientation = characteristics.cameraOrientation,
            cameraIsMirrored = characteristics.isMirrored
        )

        previewOrientation = computePreviewOrientation(
            screenOrientaion = orientationState.screenOrientation,
            cameraOrientation = characteristics.cameraOrientation,
            cameraIsMirrored = characteristics.isMirrored
        )

        previewStream.frameOrientation = previewOrientation
        //设置摄像头角度
        camera.setDisplayOrientation(displayOrientation.degrees)
    }


















}