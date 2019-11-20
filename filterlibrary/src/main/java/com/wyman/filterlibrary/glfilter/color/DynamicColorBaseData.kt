package com.wyman.filterlibrary.glfilter.color

import java.util.ArrayList

open class DynamicColorBaseData {

    var name: String? = null                         // 滤镜名称
    var vertexShader: String? = null                 // vertex shader名称
    var fragmentShader: String? = null               // fragment shader名称
    var uniformList: List<String>            // 统一变量字段列表
    var uniformDataList: List<UniformData>   // 统一变量数据列表，目前主要用于存放滤镜的png文件
    var strength: Float = 0.toFloat()                      // 默认强度
    var texelOffset: Boolean = false                 // 是否存在宽高偏移量的统一变量
    var audioPath: String? = null                    // 滤镜音乐滤镜
    var audioLooping: Boolean = false                // 音乐是否循环播放

    init {
        uniformList = ArrayList()
        uniformDataList = ArrayList()
        texelOffset = false
    }

    override fun toString(): String {
        return "DynamicColorData{" +
                "name='" + name + '\''.toString() +
                ", vertexShader='" + vertexShader + '\''.toString() +
                ", fragmentShader='" + fragmentShader + '\''.toString() +
                ", uniformList=" + uniformList +
                ", uniformDataList=" + uniformDataList +
                ", strength=" + strength +
                ", texelOffset=" + texelOffset +
                ", audioPath='" + audioPath + '\''.toString() +
                ", audioLooping=" + audioLooping +
                '}'.toString()
    }

    /**
     * 统一变量数据对象
     */
    class UniformData(var uniform: String  // 统一变量字段
                      , var value: String    // 与统一变量绑定的纹理图片
    )

}
