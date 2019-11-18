package com.bonade.fakedouyin.widget;

import android.content.Context;

import androidx.core.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wyman
 * on 2019-09-09.
 */
public class TestView extends View {
    private static final String TAG = "TestView";
    private static final boolean VERBOSE = true;

    // 手势监听器
    private GestureDetectorCompat mGestureDetector;

    // 滑动事件监听
    private OnTouchScroller mScroller;

    public TestView(Context context) {
        this(context,null);
    }

    public TestView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        mGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                if (VERBOSE) {
                    Log.d(TAG, "onDown: ");
                }

                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                if (VERBOSE) {
                    Log.d(TAG, "onShowPress: ");
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (VERBOSE) {
                    Log.d(TAG, "onSingleTapUp: ");
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (VERBOSE) {
                    Log.d(TAG, "onScroll: ");
                }

                // 上下滑动
                if (Math.abs(distanceX) < Math.abs(distanceY) * 1.5) {
                    // 是否从左边开始上下滑动
                    boolean leftScroll = e1.getX() < getWidth() / 2;
                    if (distanceY > 0) {
                        if (mScroller != null) {
                            mScroller.swipeUpper(leftScroll, Math.abs(distanceY));
                        }
                    } else {
                        if (mScroller != null) {
                            mScroller.swipeDown(leftScroll, Math.abs(distanceY));
                        }
                    }
                } else if(Math.abs(distanceX)  * 1.5 > Math.abs(distanceY)){
                    //左右滑 向右滑负，向左滑正
                    Log.e(TAG, "distanceX: " + distanceX);
                    if(mScroller != null){
                        mScroller.swipeLeftOrRight(distanceX);
                    }
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (VERBOSE) {
                    Log.d(TAG, "onLongPress: ");
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (VERBOSE) {
                    Log.d(TAG, "onFling: ");
                }
                // 快速左右滑动是
                if (Math.abs(velocityX) > Math.abs(velocityY) * 1.5) {
                    if (velocityX < 0) {
                        if (mScroller != null) {
                            mScroller.swipeBack();
                        }
                    } else {
                        if (mScroller != null) {
                            mScroller.swipeFrontal();
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    /**
     * 添加滑动回调
     * @param scroller
     */
    public void addOnTouchScroller(OnTouchScroller scroller) {
        mScroller = scroller;
    }

    /**
     * 滑动监听器
     */
    public interface OnTouchScroller {
        void swipeBack();//左滑
        void swipeFrontal();//右滑
        void swipeUpper(boolean startInLeft, float distance);
        void swipeDown(boolean startInLeft, float distance);
        void swipeLeftOrRight(float distanceX);
    }
}
