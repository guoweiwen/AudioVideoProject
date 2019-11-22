package com.wyman.cameralibrary.core.hardware

import com.wyman.cameralibrary.core.selector.ResolutionSelector
import com.wyman.cameralibrary.core.selector.highestResolution

private const val DEFAULT_JPEG_QUALITY = 90
private const val DEFAULT_EXPOSURE_COMPENSATION = 0

/**
 * 摄像头配置所有由迭代器定义
 * */
data class CameraConfiguration(
        val previewFpsRange: FpsRangeSelector = highestFps(),
        val pictureResolution : ResolutionSelector = highestResolution(),
        val previewResolution : ResolutionSelector = highestResolution()
){

}