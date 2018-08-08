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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tubingbing on 16/8/11.
 */

@SuppressWarnings("WeakerAccess")
public class SwipeMenuHelper {

    public static final int INVALID_POSITION = -1;

    protected Callback mCallback;
    protected ViewConfiguration mViewConfig;
    protected SwipeHorizontalMenuLayout mOldSwipedView;
    protected int mOldTouchedPosition = INVALID_POSITION;

    public SwipeMenuHelper(Context context, Callback callback) {
        mCallback = callback;
        mViewConfig = ViewConfiguration.get(context);
    }

    /**
     * Handle touch down event, decide whether intercept or not.
     *
     * @param ev                 Touch event
     * @param defaultIntercepted The default intercept status
     * @return Whether intercept or not
     */
    public boolean handleListDownTouchEvent(MotionEvent ev, boolean defaultIntercepted) {
        boolean isIntercepted = defaultIntercepted;
        View touchingView = findChildViewUnder((int) ev.getX(), (int) ev.getY());
        int touchingPosition;
        if (touchingView != null) {
            touchingPosition = mCallback.getPositionForView(touchingView);
        } else {
            touchingPosition = INVALID_POSITION;
        }
        if (touchingPosition != mOldTouchedPosition && mOldSwipedView != null) {
            // already one swipe menu is opened, so we close it and intercept the event
            if (mOldSwipedView.isMenuOpen()) {
                mOldSwipedView.smoothCloseMenu();
                isIntercepted = true;
            }
        }
        touchingView = mCallback.transformTouchingView(touchingPosition, touchingView);
        if (touchingView != null) {
            View itemView = getSwipeMenuView((ViewGroup) touchingView);
            if (itemView != null && itemView instanceof SwipeHorizontalMenuLayout) {
                mOldSwipedView = (SwipeHorizontalMenuLayout) itemView;
                mOldTouchedPosition = touchingPosition;
            }
        }
        // if we intercept the event, just reset
        if (isIntercepted) {
            mOldSwipedView = null;
            mOldTouchedPosition = INVALID_POSITION;
        }
        return isIntercepted;
    }

    public View getSwipeMenuView(ViewGroup itemView) {
        if (itemView instanceof SwipeHorizontalMenuLayout) {
            return itemView;
        }
        List<View> unvisited = new ArrayList<>();
        unvisited.add(itemView);
        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            if (!(child instanceof ViewGroup)) { // view
                continue;
            }
            if (child instanceof SwipeHorizontalMenuLayout) {
                return child;
            }
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                unvisited.add(group.getChildAt(i));
            }
        }
        return itemView;
    }

    /**
     * Find the topmost view under the given point.
     *
     * @param x Horizontal position in pixels to search
     * @param y Vertical position in pixels to search
     * @return The child view under (x, y) or null if no matching child is found
     */
    public View findChildViewUnder(float x, float y) {
        final int count = mCallback.getRealChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = mCallback.getRealChildAt(i);
            final float translationX = ViewCompat.getTranslationX(child);
            final float translationY = ViewCompat.getTranslationY(child);
            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }
        return null;
    }

    public interface Callback {
        int getPositionForView(View view);

        int getRealChildCount();

        View getRealChildAt(int index);

        View transformTouchingView(int touchingPosition, View touchingView);
    }

}
