package com.bonade.fakedouyin

import android.app.Application
import kotlin.properties.Delegates

/**
 * Created by wyman
on 2019-07-07.
 */
open class MyApplication : Application() {

    /**
     * 单例得到 MyApplication 对象
     * */
    companion object{
        var instance : MyApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}