package com.wyman.filterlibrary.glfilter.makeup.bean

/**
 * 彩妆数据基类
 */
class MakeupBaseData {
    // 彩妆类型
    var makeupType: MakeupType? = null
    // 彩妆名称
    var name: String? = null
    // 彩妆id，彩妆id主要是方便预设一组彩妆以及提供单独更改某个彩妆的功能，方便添加彩妆保存等功能
    var id: String? = null
    // 彩妆默认强度
    var strength: Float = 0.toFloat()

}
