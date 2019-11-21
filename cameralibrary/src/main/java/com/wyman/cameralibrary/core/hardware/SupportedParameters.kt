package com.wyman.cameralibrary.core.hardware

import android.hardware.Camera
import com.wyman.cameralibrary.core.camera.CameraParam

/**
 * 提供摄像头参数支持
 * 这里加载设备摄像头的参数
 * */
internal class SupportedParameters(
        private val cameraParameters: Camera.Parameters
){
    /**
     * @see Camera.Parameters.getSupportedFlashModes
     */
    val flashModes : List<String> by lazy {
        cameraParameters.supportedFlashModes ?: listOf(Camera.Parameters.FLASH_MODE_OFF)
    }

    /**
     * @see Camera.Parameters.getSupportedFocusModes
     */
    val focusModes : List<String> by lazy{
        cameraParameters.supportedFocusModes
    }

    /**
     * @see Camera.Parameters.getSupportedPreviewSizes
     */
    val previewResolutions : List<Camera.Size> by lazy {
        cameraParameters.supportedPreviewSizes
    }

    /**
     * @see Camera.Parameters.getSupportedPictureSizes
     */
    val pictureResolutions : List<Camera.Size> by lazy {
        cameraParameters.supportedPictureSizes
    }

    /**
     * @see Camera.Parameters.getSupportedPreviewFpsRange
     */
    val supportedPreviewFpsRanges : List<IntArray> by lazy{
        cameraParameters.supportedPreviewFpsRange
    }

    /**
     * @return The list of supported sensitivities (ISO) by the camera.
     */
    val sensorSensitives : List<Int> by lazy{
        cameraParameters.extractRawCameraValues(supportedSensitivitiesKeys).toInts()
    }


}

//数组
private val supportedSensitivitiesKeys = listOf("iso-values", "iso-mode-values", "iso-speed-values", "nv-picture-iso-values")

internal fun Camera.Parameters.extractRawCameraValues(keys : List<String>): List<String>{
    keys.forEach {
        key -> getValuesForKey(key)?.let{
            //将 参数用 "，"拆开放进List里面
            params -> return params
        }
    }
    return emptyList()
}

/**
 * Converts a [List] of [String] to a [List] of [Int], if possible.
 * If no value can be converted to [Int], empty list will be returned.
 */
internal fun List<String>.toInts() = mapNotNull {
    try {
        it.trim().toInt()
    } catch (e: NumberFormatException) {
        // Found not number option. Skip it.
        null
    }
}

private fun Camera.Parameters.getValuesForKey(key : String): List<String>? =
        get(key)?.split(regex = ",".toRegex())