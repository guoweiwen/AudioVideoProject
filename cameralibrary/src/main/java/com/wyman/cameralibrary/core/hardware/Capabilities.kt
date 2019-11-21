package com.wyman.cameralibrary.core.hardware

import android.hardware.Camera


/**
 * 设备提供摄像头的参数
 * 不能保证所有值都有
 * */
data class Capabilities (
        //val zoom : Zoom,
        //val flashMode : Set<Flash>,
        val previewFpsRanges : Set<FpsRange>,
        val pictureResolution : Set<Resolution>,
        val previewResolution : Set<Resolution>
){
    init {
        pictureResolution.ensureNotEmpty()
        previewFpsRanges.ensureNotEmpty()
        previewResolution.ensureNotEmpty()
    }

}

/**
 * 内联函数
 * */
private inline fun <reified E>Set<E>.ensureNotEmpty(){
    //isEmpty 为 Collections 的方法
    require(!isEmpty()) { "Capabilities cannot have an empty Set<${E::class.java.simpleName}>." }
    //等同于
//    if(isEmpty()){
//        throw IllegalArgumentException("")
//    }
}

internal fun Camera.getCapabilities() = SupportedParameters(parameters).getCapabilities()

private fun SupportedParameters.getCapabilities() : Capabilities{
    return Capabilities(
        //将设备摄像头的参数存放在 Capabilities
        previewFpsRanges = supportedPreviewFpsRanges.extract{
            it.toFpsRange()
        },
        pictureResolution = pictureResolutions.mapSizes(),
        previewResolution = previewResolutions.mapSizes()
    )
}

/**
 * 将 list 转为 set
 * */
private fun <Parameter : Any,Code>List<Code>.extract(converter: (Code)->Parameter?)
    = mapNotNull { converter(it) }.toSet()


private fun Collection<Camera.Size>.mapSizes() = map{it.toResolution()}.toSet()

/**
 * 将 Int数组 转为 FpsRangs
 * */
internal fun IntArray.toFpsRange() : FpsRange =
        FpsRange(
                this[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                this[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
        )

fun Camera.Size.toResolution() : Resolution = Resolution(width,height)













