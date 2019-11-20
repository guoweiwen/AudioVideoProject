package com.wyman.filterlibrary.glfilter.makeup.bean

import java.util.ArrayList

/**
 * 动态彩妆
 */
class DynamicMakeup {

    // 彩妆解压的文件夹路径
    var unzipPath: String? = null
    // 彩妆列表
    var makeupList: List<MakeupBaseData>

    init {
        unzipPath = null
        makeupList = ArrayList()
    }

    override fun toString(): String {
        return "DynamicMakeup{" +
                "unzipPath='" + unzipPath + '\''.toString() +
                ", makeupList=" + makeupList +
                '}'.toString()
    }
}
