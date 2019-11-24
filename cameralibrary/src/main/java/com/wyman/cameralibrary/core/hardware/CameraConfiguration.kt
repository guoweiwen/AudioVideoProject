package com.wyman.cameralibrary.core.hardware

import com.wyman.cameralibrary.core.selector.FpsRangeSelector
import com.wyman.cameralibrary.core.selector.ResolutionSelector
import com.wyman.cameralibrary.core.selector.highestFps
import com.wyman.cameralibrary.core.selector.highestResolution
import com.wyman.cameralibrary.preview.FrameProcessor
import com.wyman.cameralibrary.frame.FrameProcessor as FrameProcessorJava

private const val DEFAULT_JPEG_QUALITY = 90
private const val DEFAULT_EXPOSURE_COMPENSATION = 0

/**
 * 摄像头配置所有由迭代器定义
 * */
data class CameraConfiguration(
        val previewFpsRange: FpsRangeSelector = highestFps(),//默认值
        val pictureResolution : ResolutionSelector = highestResolution(),//默认值
        val previewResolution : ResolutionSelector = highestResolution(),//默认值
        var frameProcessor: FrameProcessor? = null
){
    //Builder 模式
    class Builder internal constructor(){
        //这个对象在类加载就初始化出来了
        private var cameraConfiguration : CameraConfiguration = default()

        fun previewFpsRange(selector: FpsRangeSelector) : Builder = apply{
            //copy函数 就是为了更改对象中某个字段
            cameraConfiguration = cameraConfiguration.copy(previewFpsRange = selector)
        }

        fun previewResolution(selector: ResolutionSelector) : Builder = apply {
            cameraConfiguration = cameraConfiguration.copy(
                    pictureResolution = selector
            )
        }

        /**
         * FrameProcessorJava 为 java 接口
         * */
        fun frameProcessor(frameProcessor: FrameProcessorJava) : Builder = apply{
            cameraConfiguration = cameraConfiguration.copy(
                    frameProcessor = frameProcessor?.let { it::process }
            )
        }

        fun build() : CameraConfiguration = cameraConfiguration
    }

    companion object{
        @JvmStatic
        fun default() : CameraConfiguration = CameraConfiguration()

        @JvmStatic
        fun build() : Builder = Builder()
    }
}