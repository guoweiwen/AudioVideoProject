package com.wyman.cameralibrary.core.selector

import com.wyman.utillibrary.selector.single


//typealias 为别名 即  CameraSelector 等同于 通过 Iterable 迭代器找 LensPosition
typealias CameraSelector = Iterable<com.wyman.cameralibrary.core.characteristic.LensPosition>.() -> com.wyman.cameralibrary.core.characteristic.LensPosition?

//获取前置摄像头对象
fun front() : CameraSelector = single(com.wyman.cameralibrary.core.characteristic.LensPosition.Front)

//获取后置摄像头
fun back() : CameraSelector = single(com.wyman.cameralibrary.core.characteristic.LensPosition.Back)

