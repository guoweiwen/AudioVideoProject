package com.bonade.fakedouyin.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.core.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import com.bonade.fakedouyin.R
import com.bonade.fakedouyin.utils.dp2px


/**
 * OpenGL录制视频控件
 * @date 2019/7/7
 */
class GLRecordView : GLSurfaceView {

    // 单击时点击的位置
    private var mTouchX = 0f
    private var mTouchY = 0f

    // 对焦动画
    private var mFocusAnimator: ValueAnimator? = null
    // 对焦图片
    private var mFocusImageView: ImageView? = null
    // 滑动事件监听
    private var mScroller: OnTouchScroller? = null
    // 单双击事件监听
    private var mMultiClickListener: OnMultiClickListener? = null

    // 手势监听器
    private var mGestureDetector: GestureDetectorCompat? = null

    /**
     * 双击监听器
     */
    private val mDoubleTapListener = object : GestureDetector.OnDoubleTapListener {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (VERBOSE) {
                Log.d(TAG, "onSingleTapConfirmed: ")
            }
            mTouchX = e.x
            mTouchY = e.y
            if (mMultiClickListener != null) {
                mMultiClickListener!!.onSurfaceSingleClick(e.x, e.y)
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (VERBOSE) {
                Log.d(TAG, "onDoubleTap: ")
            }
            if (mMultiClickListener != null) {
                mMultiClickListener!!.onSurfaceDoubleClick(e.x, e.y)
            }
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            if (VERBOSE) {
                Log.d(TAG, "onDoubleTapEvent: ")
            }
            return true
        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        isClickable = true
        mGestureDetector = GestureDetectorCompat(context, object : GestureDetector.OnGestureListener {
            override fun onDown(e: MotionEvent): Boolean {
                if (VERBOSE) {
                    Log.d(TAG, "onDown: ")
                }

                return false
            }

            override fun onShowPress(e: MotionEvent) {
                if (VERBOSE) {
                    Log.d(TAG, "onShowPress: ")
                }
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (VERBOSE) {
                    Log.d(TAG, "onSingleTapUp: ")
                }
                return false
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                if (VERBOSE) {
                    Log.d(TAG, "onScroll: ")
                }

                // 上下滑动
                if (Math.abs(distanceX) < Math.abs(distanceY) * 1.5) {
                    // 是否从左边开始上下滑动
                    val leftScroll = e1.x < width / 2
                    if (distanceY > 0) {
                        if (mScroller != null) {
                            mScroller!!.swipeUpper(leftScroll, Math.abs(distanceY))
                        }
                    } else {
                        if (mScroller != null) {
                            mScroller!!.swipeDown(leftScroll, Math.abs(distanceY))
                        }
                    }
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                if (VERBOSE) {
                    Log.d(TAG, "onLongPress: ")
                }
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (VERBOSE) {
                    Log.d(TAG, "onFling: ")
                }
                // 快速左右滑动是
                if (Math.abs(velocityX) > Math.abs(velocityY) * 1.5) {
                    if (velocityX < 0) {
                        if (mScroller != null) {
                            Log.e(TAG, "swipeBack: ")
                            mScroller!!.swipeBack()
                        }
                    } else {
                        if (mScroller != null) {
                            Log.e(TAG, "swipeFrontal: ")
                            mScroller!!.swipeFrontal()
                        }
                    }
                }
                return false
            }
        })
        mGestureDetector!!.setOnDoubleTapListener(mDoubleTapListener)

        // 添加圆角显示
        if (Build.VERSION.SDK_INT >= 21) {
            outlineProvider = RoundOutlineProvider(dp2px(7.5f))
            clipToOutline = true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mGestureDetector!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    /**
     * 添加对焦动画
     */
    fun showFocusAnimation() {
        if (mFocusAnimator == null) {
            mFocusImageView = ImageView(context)
            mFocusImageView!!.setImageResource(R.mipmap.video_focus)
            mFocusImageView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mFocusImageView!!.measure(0, 0)
            mFocusImageView!!.x = mTouchX - mFocusImageView!!.measuredWidth / 2
            mFocusImageView!!.y = mTouchY - mFocusImageView!!.measuredHeight / 2
            val parent = parent as ViewGroup
            parent.addView(mFocusImageView)

            mFocusAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(500)
            mFocusAnimator!!.addUpdateListener { animation ->
                if (mFocusImageView != null) {
                    val value = animation.animatedValue as Float
                    mFocusImageView!!.scaleX = 2 - value
                    mFocusImageView!!.scaleY = 2 - value
                }
            }
            mFocusAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (mFocusImageView != null) {
                        parent.removeView(mFocusImageView)
                        mFocusAnimator = null
                    }
                }
            })
            mFocusAnimator!!.start()
        }
    }


    /**
     * 添加视频圆角功能
     */
    private class RoundOutlineProvider internal constructor(private val mRadius: Float) : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            val leftMargin = 0
            val topMargin = 0
            val selfRect = Rect(leftMargin, topMargin,
                    rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin)
            outline.setRoundRect(selfRect, mRadius)
        }
    }

    /**
     * 添加滑动回调
     * @param scroller
     */
    fun addOnTouchScroller(scroller: OnTouchScroller) {
        mScroller = scroller
    }

    /**
     * 滑动监听器
     */
    interface OnTouchScroller {
        fun swipeBack()
        fun swipeFrontal()
        fun swipeUpper(startInLeft: Boolean, distance: Float)
        fun swipeDown(startInLeft: Boolean, distance: Float)
    }

    /**
     * 添加点击事件回调
     * @param listener
     */
    fun addMultiClickListener(listener: OnMultiClickListener) {
        mMultiClickListener = listener
    }

    /**
     * 点击事件监听器
     */
    interface OnMultiClickListener {
        // 单击
        fun onSurfaceSingleClick(x: Float, y: Float)

        // 双击
        fun onSurfaceDoubleClick(x: Float, y: Float)
    }

    companion object {
        private val TAG = "CainSurfaceView"
        private val VERBOSE = false
    }
}
