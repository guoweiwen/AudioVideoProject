package com.wyman.cameralibrary.preview

import android.graphics.ImageFormat
import android.hardware.Camera
import com.wyman.cameralibrary.core.hardware.Resolution
import com.wyman.cameralibrary.core.hardware.frameProcessingExecutors
import com.wyman.cameralibrary.core.orientation.Orientation
import java.lang.IllegalArgumentException

//FrameProcessor 变量为回调函数
typealias FrameProcessor = (frame : Frame) -> Unit
/**
 * 摄像头预览流
 * */
internal class PreviewStream (private val camera : Camera){
    private val frameProcessors = LinkedHashSet<FrameProcessor>()
    private var previewResolution : Resolution? = null

    /**
     * 逆时针方向
     * */
    var frameOrientation : Orientation = Orientation.Vertical.Portrait

    /**
     * Clears all processors
     * 清除所有进行的 任务
     * */
    private fun clearProcessors(){
        synchronized(frameProcessors) {
            frameProcessors.clear()
        }
    }

    /**
     * 注册一个新的任务；如果任务之前就有就什么都不做
     * 所以用了 LinkedHashSet 集合
     * */
    private fun addProcessor(processor: FrameProcessor){
        synchronized(frameProcessors){
            frameProcessors.add(processor)
        }
    }

    /**
     * 开启预览流，在开始预览之后接收 帧数据
     * */
    private fun start(){
        //添加缓冲区
        camera.addFrameToBuffer()

        camera.setPreviewCallbackWithBuffer{
            data, _ -> dispatchFrameOnBackgroundThread(data)
        }
    }

    /**
     * 停止预览流数据
     * */
    private fun stop(){
        camera.setPreviewCallbackWithBuffer(null)
    }

    private fun dispatchFrameOnBackgroundThread(data : ByteArray){
        frameProcessingExecutors.execute {
            synchronized(frameProcessors){
                dispatchFrame(data)
            }
        }
    }

    /**
     * 分发帧数据
     * */
    private fun dispatchFrame(image : ByteArray){
        val previewResolution = ensurePreviewSizeAvailable()
        val frame = Frame(
                size = previewResolution,
                image = image,
                rotation = frameOrientation.degrees
        )
        frameProcessors.forEach {
            //回调每个任务
            it.invoke(frame)
        }
        //将字节数组放回缓冲区
        returnFrameToBuffer(frame)
    }

    /**
     * 重新往缓冲区填入数据
     * */
    private fun returnFrameToBuffer(frame: Frame){
        camera.addCallbackBuffer(frame.image)
    }

    private fun ensurePreviewSizeAvailable() : Resolution =
            //没有 previewResolution 即抛出异常
            previewResolution ?: throw IllegalArgumentException("previewSize is null. Frame was not added?")

    private fun Camera.addFrameToBuffer(){
        //parameters 是Camera内面的内部类
        addCallbackBuffer(parameters.allocateBuffer())
    }

    /**
     * 创建缓冲区字节数组
     * */
    private fun Camera.Parameters.allocateBuffer() : ByteArray{
        ensureNv21Format()
        //previewSize 为 Camera内部类
        previewResolution = Resolution(previewSize.width,previewSize.height)
        return ByteArray(previewSize.bytePerFrame())
    }
}

private fun Camera.Size.bytePerFrame() : Int =
        //ImageFormat.getBitsPerPixel 返回每个格式占的字节数 NV21 每个像素占12bits（8bits 为1字节）
        width * height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8//除8是因为转为多少字节

private fun Camera.Parameters.ensureNv21Format(){
    require(previewFormat == ImageFormat.NV21) { "Only NV21 preivew format is supported" }
}
















