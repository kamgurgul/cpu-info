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
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SwipeMenuRecyclerView extends RecyclerView implements SwipeMenuHelper.Callback {

    protected SwipeMenuHelper mHelper;

    public SwipeMenuRecyclerView(Context context) {
        super(context);
        init();
    }

    public SwipeMenuRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeMenuRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        mHelper = new SwipeMenuHelper(getContext(), this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercepted = super.onInterceptTouchEvent(ev);
        // ignore Multi-Touch
        if (ev.getActionIndex() != 0) return true;
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isIntercepted = mHelper.handleListDownTouchEvent(ev, isIntercepted);
                break;
        }
        return isIntercepted;
    }

    public int getPositionForView(View touchView) {
        return getChildAdapterPosition(touchView);
    }

    @Override
    public int getRealChildCount() {
        return getChildCount();
    }

    @Override
    public View getRealChildAt(int index) {
        return getChildAt(index);
    }

    @Override
    public View transformTouchingView(int touchingPosition, View touchingView) {
        ViewHolder vh = findViewHolderForAdapterPosition(touchingPosition);
        if (vh != null) {
            return vh.itemView;
        }
        return touchingView;
    }
}
