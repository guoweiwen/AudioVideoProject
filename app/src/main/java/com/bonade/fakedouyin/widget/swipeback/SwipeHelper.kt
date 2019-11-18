package com.bonade.fakedouyin.widget.swipeback

import android.app.Activity
import android.view.LayoutInflater



/**
 * Created by wyman
 * on 2019-07-19.
 * SwipeBackLayout 的辅助类
 */
class SwipeHelper constructor(var mActivity : Activity) {
    private var mBaseSwipeLayout : SwipeBackLayout? = null

    fun onActivityCreate(){
        mBaseSwipeLayout = LayoutInflater.from(mActivity)
                .inflate(com.bonade.fakedouyin.R.layout.swipe_layout,null)
                as SwipeBackLayout

        mBaseSwipeLayout!!.setOnFinishScroll(object : SwipeBackLayout.OnFinishScroll {
            override fun complete() {
                mActivity.finish()
            }
        })
    }

    /**
     * 该方法后于 onActivityCreate 执行
     * */
    fun onPostCreate(){
        //将 SwipeBackLayout 作为 DecorView的子View添加进去
        mBaseSwipeLayout!!.attachToActivity(mActivity)
    }

    fun setSwipeEdge(edgeFlag : Int){
        mBaseSwipeLayout!!.setSwipeEdge(edgeFlag)
    }
}










