package com.bonade.fakedouyin.chossice_music

import android.os.Bundle
import androidx.customview.widget.ViewDragHelper
import com.bonade.fakedouyin.BaseActivity
import com.bonade.fakedouyin.R
import com.bonade.fakedouyin.widget.swipeback.SwipeHelper

/**
 * Created by wyman
 *  on 2019-07-18.
 *  选择音乐界面
 */
class ChossiceMusicActivity : BaseActivity() {
    var mSwipeHelper : SwipeHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chossicemusic)

        mSwipeHelper = SwipeHelper(this)
        mSwipeHelper!!.onActivityCreate()
        mSwipeHelper!!.setSwipeEdge(ViewDragHelper.EDGE_TOP)
    }

    /**
     * 后于 onCreate 方法执行
     * */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if(mSwipeHelper != null){
            mSwipeHelper!!.onPostCreate()
        }
    }

    companion object{
        const val TAG = "ChossiceMusicActivity"
    }
}