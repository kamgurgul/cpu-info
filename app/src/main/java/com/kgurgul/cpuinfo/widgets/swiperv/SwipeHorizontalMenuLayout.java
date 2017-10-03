/*
 * Copyright 2017 KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kgurgul.cpuinfo.widgets.swiperv;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewParent;

import com.kgurgul.cpuinfo.R;
import com.kgurgul.cpuinfo.widgets.swiperv.listener.SwipeSwitchListener;
import com.kgurgul.cpuinfo.widgets.swiperv.swiper.LeftHorizontalSwiper;
import com.kgurgul.cpuinfo.widgets.swiperv.swiper.RightHorizontalSwiper;
import com.kgurgul.cpuinfo.widgets.swiperv.swiper.Swiper;

public class SwipeHorizontalMenuLayout extends SwipeMenuLayout {

    protected int mPreScrollX;
    protected float mPreLeftMenuFraction = -1;
    protected float mPreRightMenuFraction = -1;

    public SwipeHorizontalMenuLayout(Context context) {
        super(context);
    }

    public SwipeHorizontalMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeHorizontalMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercepted = super.onInterceptTouchEvent(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = mLastX = (int) ev.getX();
                mDownY = (int) ev.getY();
                isIntercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) (ev.getX() - mDownX);
                int disY = (int) (ev.getY() - mDownY);
                isIntercepted = Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY);
                break;
            case MotionEvent.ACTION_UP:
                isIntercepted = false;
                // menu view opened and click on content view,
                // we just close the menu view and intercept the up event
                if (isMenuOpen()
                        && mCurrentSwiper.isClickOnContentView(this, ev.getX())) {
                    smoothCloseMenu();
                    isIntercepted = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isIntercepted = false;
                if (!mScroller.isFinished())
                    mScroller.forceFinished(false);
                break;
        }
        return isIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(ev);
        int dx;
        int dy;
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isSwipeEnable()) break;
                int disX = (int) (mLastX - ev.getX());
                int disY = (int) (mLastY - ev.getY());
                if (!mDragging
                        && Math.abs(disX) > mScaledTouchSlop
                        && Math.abs(disX) > Math.abs(disY)) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mDragging = true;
                }
                if (mDragging) {
                    if (mCurrentSwiper == null || shouldResetSwiper) {
                        if (disX < 0) {
                            if (mBeginSwiper != null)
                                mCurrentSwiper = mBeginSwiper;
                            else
                                mCurrentSwiper = mEndSwiper;
                        } else {
                            if (mEndSwiper != null)
                                mCurrentSwiper = mEndSwiper;
                            else
                                mCurrentSwiper = mBeginSwiper;
                        }
                    }
                    scrollBy(disX, 0);
                    mLastX = (int) ev.getX();
                    mLastY = (int) ev.getY();
                    shouldResetSwiper = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
                dx = (int) (mDownX - ev.getX());
                dy = (int) (mDownY - ev.getY());
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                int velocity = Math.abs(velocityX);
                if (velocity > mScaledMinimumFlingVelocity) {
                    if (mCurrentSwiper != null) {
                        int duration = getSwipeDuration(ev, velocity);
                        if (mCurrentSwiper instanceof RightHorizontalSwiper) {
                            if (velocityX < 0) { // just open
                                smoothOpenMenu(duration);
                            } else { // just close
                                smoothCloseMenu(duration);
                            }
                        } else {
                            if (velocityX > 0) { // just open
                                smoothOpenMenu(duration);
                            } else { // just close
                                smoothCloseMenu(duration);
                            }
                        }
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                } else {
                    judgeOpenClose(dx, dy);
                }
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                if (Math.abs(dx) > mScaledTouchSlop
                        || Math.abs(dy) > mScaledTouchSlop
                        || isMenuOpen()) { // ignore click listener, cancel this event
                    MotionEvent motionEvent = MotionEvent.obtain(ev);
                    motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onTouchEvent(motionEvent);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(false);
                } else {
                    dx = (int) (mDownX - ev.getX());
                    dy = (int) (mDownY - ev.getY());
                    judgeOpenClose(dx, dy);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void judgeOpenClose(int dx, int dy) {
        if (mCurrentSwiper != null) {
            if (Math.abs(getScrollX()) >= (mCurrentSwiper.getMenuView().getWidth() * mAutoOpenPercent)) { // auto open
                if (Math.abs(dx) > mScaledTouchSlop || Math.abs(dy) > mScaledTouchSlop) { // swipe up
                    if (isMenuOpenNotEqual()) smoothCloseMenu();
                    else smoothOpenMenu();
                } else { // normal up
                    if (isMenuOpen()) smoothCloseMenu();
                    else smoothOpenMenu();
                }
            } else { // auto close
                smoothCloseMenu();
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        Swiper.Checker checker = mCurrentSwiper.checkXY(x, y);
        shouldResetSwiper = checker.shouldResetSwiper;
        if (checker.x != getScrollX()) {
            super.scrollTo(checker.x, checker.y);
        }
        if (getScrollX() != mPreScrollX) {
            int absScrollX = Math.abs(getScrollX());
            if (mCurrentSwiper instanceof LeftHorizontalSwiper) {
                if (mSwipeSwitchListener != null) {
                    if (absScrollX == 0) mSwipeSwitchListener.beginMenuClosed(this);
                    else if (absScrollX == mBeginSwiper.getMenuWidth())
                        mSwipeSwitchListener.beginMenuOpened(this);
                }
                if (mSwipeFractionListener != null) {
                    float fraction = (float) absScrollX / mBeginSwiper.getMenuWidth();
                    fraction = Float.parseFloat(mDecimalFormat.format(fraction));
                    if (fraction != mPreLeftMenuFraction) {
                        mSwipeFractionListener.beginMenuSwipeFraction(this, fraction);
                    }
                    mPreLeftMenuFraction = fraction;
                }
            } else {
                if (mSwipeSwitchListener != null) {
                    if (absScrollX == 0) mSwipeSwitchListener.endMenuClosed(this);
                    else if (absScrollX == mEndSwiper.getMenuWidth())
                        mSwipeSwitchListener.endMenuOpened(this);
                }
                if (mSwipeFractionListener != null) {
                    float fraction = (float) absScrollX / mEndSwiper.getMenuWidth();
                    fraction = Float.parseFloat(mDecimalFormat.format(fraction));
                    if (fraction != mPreRightMenuFraction) {
                        mSwipeFractionListener.endMenuSwipeFraction(this, fraction);
                    }
                    mPreRightMenuFraction = fraction;
                }
            }
        }
        mPreScrollX = getScrollX();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = Math.abs(mScroller.getCurrX());
            if (mCurrentSwiper instanceof RightHorizontalSwiper) {
                scrollTo(currX, 0);
                invalidate();
            } else {
                scrollTo(-currX, 0);
                invalidate();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
        mContentView = findViewById(R.id.smContentView);
        if (mContentView == null) {
            throw new IllegalArgumentException("Not find contentView by id smContentView");
        }
        View menuViewLeft = findViewById(R.id.smMenuViewLeft);
        View menuViewRight = findViewById(R.id.smMenuViewRight);
        if (menuViewLeft == null && menuViewRight == null) {
            throw new IllegalArgumentException("Not find menuView by id (smMenuViewLeft, smMenuViewRight)");
        }
        if (menuViewLeft != null) mBeginSwiper = new LeftHorizontalSwiper(menuViewLeft);
        if (menuViewRight != null) mEndSwiper = new RightHorizontalSwiper(menuViewRight);
    }

    public boolean isMenuOpen() {
        return (mBeginSwiper != null && mBeginSwiper.isMenuOpen(getScrollX()))
                || (mEndSwiper != null && mEndSwiper.isMenuOpen(getScrollX()));
    }

    public boolean isMenuOpenNotEqual() {
        return (mBeginSwiper != null && mBeginSwiper.isMenuOpenNotEqual(getScrollX()))
                || (mEndSwiper != null && mEndSwiper.isMenuOpenNotEqual(getScrollX()));
    }

    public void smoothOpenMenu(int duration) {
        mCurrentSwiper.autoOpenMenu(mScroller, getScrollX(), duration);
        invalidate();
    }

    public void smoothCloseMenu(int duration) {
        mCurrentSwiper.autoCloseMenu(mScroller, getScrollX(), duration);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int parentViewWidth = ViewCompat.getMeasuredWidthAndState(this);
        int contentViewWidth = ViewCompat.getMeasuredWidthAndState(mContentView);
        int contentViewHeight = ViewCompat.getMeasuredHeightAndState(mContentView);
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        int lGap = getPaddingLeft() + lp.leftMargin;
        int tGap = getPaddingTop() + lp.topMargin;
        mContentView.layout(lGap,
                tGap,
                lGap + contentViewWidth,
                tGap + contentViewHeight);
        if (mEndSwiper != null) {
            int menuViewWidth = ViewCompat.getMeasuredWidthAndState(mEndSwiper.getMenuView());
            int menuViewHeight = ViewCompat.getMeasuredHeightAndState(mEndSwiper.getMenuView());
            lp = (LayoutParams) mEndSwiper.getMenuView().getLayoutParams();
            tGap = getPaddingTop() + lp.topMargin;
            mEndSwiper.getMenuView().layout(parentViewWidth,
                    tGap,
                    parentViewWidth + menuViewWidth,
                    tGap + menuViewHeight);
        }
        if (mBeginSwiper != null) {
            int menuViewWidth = ViewCompat.getMeasuredWidthAndState(mBeginSwiper.getMenuView());
            int menuViewHeight = ViewCompat.getMeasuredHeightAndState(mBeginSwiper.getMenuView());
            lp = (LayoutParams) mBeginSwiper.getMenuView().getLayoutParams();
            tGap = getPaddingTop() + lp.topMargin;
            mBeginSwiper.getMenuView().layout(-menuViewWidth,
                    tGap,
                    0,
                    tGap + menuViewHeight);
        }
    }

    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    public void setSwipeListener(SwipeSwitchListener swipeSwitchListener) {
        mSwipeSwitchListener = swipeSwitchListener;
    }

    int getLen() {
        return mCurrentSwiper.getMenuWidth();
    }

    int getMoveLen(MotionEvent ev) {
        int sx = getScrollX();
        return (int) (ev.getX() - sx);
    }

}
