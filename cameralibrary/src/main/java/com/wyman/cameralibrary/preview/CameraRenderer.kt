package com.wyman.cameralibrary.preview

import com.wyman.cameralibrary.core.hardware.Resolution

/**
 * 从摄像头获取数据渲染流
 */
interface CameraRenderer {

    /**
     * Sets the scale type of the preview to the renderer. This method will be called from camera
     * thread, so it is safe to perform blocking operations here.
     */
    fun setScaleType(scaleType: ScaleType)

    /**
     * 显示分辨率
     */
    fun setPreviewResolution(resolution: Resolution)

    /**
     * Returns the surface texture when available.
     * 当可用时候返回 surface 纹理
     */
    fun getPreview(): Preview

}