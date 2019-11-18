package com.bonade.filterlibrary.beauty.bean

/**
 * 美颜参数
 */
class BeautyParam {
    // 磨皮程度 0.0 ~ 1.0f
    var beautyIntensity: Float = 0.toFloat()
    // 美肤程度 0.0 ~ 0.5f
    var complexionIntensity: Float = 0.toFloat()
    // 瘦脸程度 0.0 ~ 1.0f
    var faceLift: Float = 0.toFloat()
    // 削脸程度 0.0 ~ 1.0f
    var faceShave: Float = 0.toFloat()
    // 小脸 0.0 ~ 1.0f
    var faceNarrow: Float = 0.toFloat()
    // 下巴-1.0f ~ 1.0f
    var chinIntensity: Float = 0.toFloat()
    // 法令纹 0.0 ~ 1.0f
    var nasolabialFoldsIntensity: Float = 0.toFloat()
    // 额头 -1.0f ~ 1.0f
    var foreheadIntensity: Float = 0.toFloat()
    // 大眼 0.0f ~ 1.0f
    var eyeEnlargeIntensity: Float = 0.toFloat()
    // 眼距 -1.0f ~ 1.0f
    var eyeDistanceIntensity: Float = 0.toFloat()
    // 眼角 -1.0f ~ 1.0f
    var eyeCornerIntensity: Float = 0.toFloat()
    // 卧蚕 0.0f ~ 1.0f
    var eyeFurrowsIntensity: Float = 0.toFloat()
    // 眼袋 0.0 ~ 1.0f
    var eyeBagsIntensity: Float = 0.toFloat()
    // 亮眼 0.0 ~ 1.0f
    var eyeBrightIntensity: Float = 0.toFloat()
    // 瘦鼻 0.0 ~ 1.0f
    var noseThinIntensity: Float = 0.toFloat()
    // 鼻翼 0.0 ~ 1.0f
    var alaeIntensity: Float = 0.toFloat()
    // 长鼻子 0.0 ~ 1.0f
    var proboscisIntensity: Float = 0.toFloat()
    // 嘴型 0.0 ~ 1.0f;
    var mouthEnlargeIntensity: Float = 0.toFloat()
    // 美牙 0.0 ~ 1.0f
    var teethBeautyIntensity: Float = 0.toFloat()

    init {
        reset()
    }

    /**
     * 重置为默认参数
     */
    fun reset() {
        beautyIntensity = 0.5f
        complexionIntensity = 0.5f
        faceLift = 0.0f
        faceShave = 0.0f
        faceNarrow = 0.0f
        chinIntensity = 0.0f
        nasolabialFoldsIntensity = 0.0f
        foreheadIntensity = 0.0f
        eyeEnlargeIntensity = 0.0f
        eyeDistanceIntensity = 0.0f
        eyeCornerIntensity = 0.0f
        eyeFurrowsIntensity = 0.0f
        eyeBagsIntensity = 0.0f
        eyeBrightIntensity = 0.0f
        noseThinIntensity = 0.0f
        alaeIntensity = 0.0f
        proboscisIntensity = 0.0f
        mouthEnlargeIntensity = 0.0f
        teethBeautyIntensity = 0.0f
    }
}
