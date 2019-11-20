package com.wyman.filterlibrary.glfilter.color

import java.util.ArrayList

/**
 * 滤镜数据
 */
class DynamicColor {

    // 滤镜解压的文件夹路径
    var unzipPath: String? = null

    // 滤镜列表
    var filterList: List<DynamicColorData>

    init {
        filterList = ArrayList<DynamicColorData>()
    }

    override fun toString(): String {
        val builder = StringBuilder()

        builder.append("unzipPath: ")
        builder.append(unzipPath)
        builder.append("\n")

        builder.append("data: [")
        for (i in filterList.indices) {
            builder.append(filterList[i].toString())
            if (i < filterList.size - 1) {
                builder.append(",")
            }
        }
        builder.append("]")

        return builder.toString()
    }

}
