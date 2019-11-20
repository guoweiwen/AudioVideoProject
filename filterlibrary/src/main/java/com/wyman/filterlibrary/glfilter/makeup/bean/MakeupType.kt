package com.wyman.filterlibrary.glfilter.makeup.bean

/**
 * 彩妆类型
 */
enum class MakeupType constructor(// 口红/唇彩

         var name_: String, var index: Int) {

    NONE("none", -1), // 无彩妆
    SHADOW("shadow", 0), // 阴影
    PUPIL("pupil", 1), // 瞳孔
    EYESHADOW("eyeshadow", 2), // 眼影
    EYELINER("eyeliner", 3), // 眼线
    EYELASH("eyelash", 4), // 睫毛
    EYELID("eyelid", 5), // 眼皮
    EYEBROW("eyebrow", 6), // 眉毛
    BLUSH("blush", 7), // 腮红
    LIPSTICK("lipstick", 8);

    /**
     * 彩妆索引
     */
    object MakeupIndex {
        val LipstickIndex = 0  // 口红/唇彩
        val BlushIndex = 1     // 腮红
        val ShadowIndex = 2    // 阴影
        val EyebrowIndex = 3   // 眉毛
        val EyeshadowIndex = 4 // 眼影
        val EyelinerIndex = 5  // 眼线
        val EyelashIndex = 6   // 睫毛
        val EyelidIndex = 7    // 眼皮
        val PupilIndex = 8     // 瞳孔
        val MakeupSize = 9     // 支持的彩妆数量
    }

    companion object {

        /**
         * 根据名称取得类型
         * @param name
         * @return
         */
        fun getType(name: String): MakeupType {
            when (name) {
                "shadow" -> return SHADOW

                "pupil" -> return PUPIL

                "eyeshadow" -> return EYESHADOW

                "eyeliner" -> return EYELINER

                "eyelash" -> return EYELASH

                "eyelid" -> return EYELID

                "eyebrow" -> return EYEBROW

                "blush" -> return BLUSH

                "lipstick" -> return LIPSTICK

                else -> return NONE
            }
        }

        /**
         * 根据索引取得类型
         * @param index
         * @return
         */
        fun getType(index: Int): MakeupType {
            when (index) {
                0 -> return SHADOW

                1 -> return PUPIL

                2 -> return EYESHADOW

                3 -> return EYELINER

                4 -> return EYELASH

                5 -> return EYELID

                6 -> return EYEBROW

                7 -> return BLUSH

                8 -> return LIPSTICK

                else -> return NONE
            }
        }
    }

}
