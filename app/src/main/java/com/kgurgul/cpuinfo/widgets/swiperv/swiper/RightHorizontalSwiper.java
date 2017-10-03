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

package com.kgurgul.cpuinfo.widgets.swiperv.swiper;

import android.view.View;
import android.widget.OverScroller;

/**
 * Created by tubingbing on 16/4/11.
 */
public class RightHorizontalSwiper extends Swiper {

    public RightHorizontalSwiper(View menuView) {
        super(END_DIRECTION, menuView);
    }

    @Override
    public boolean isMenuOpen(int scrollX) {
        return scrollX >= -getMenuView().getWidth() * getDirection();
    }

    @Override
    public boolean isMenuOpenNotEqual(int scrollX) {
        return scrollX > -getMenuView().getWidth() * getDirection();
    }

    @Override
    public void autoOpenMenu(OverScroller scroller, int scrollX, int duration) {
        scroller.startScroll(Math.abs(scrollX), 0, getMenuView().getWidth() - Math.abs(scrollX), 0, duration);
    }

    @Override
    public void autoCloseMenu(OverScroller scroller, int scrollX, int duration) {
        scroller.startScroll(-Math.abs(scrollX), 0, Math.abs(scrollX), 0, duration);
    }

    @Override
    public Checker checkXY(int x, int y) {
        mChecker.x = x;
        mChecker.y = y;
        mChecker.shouldResetSwiper = false;
        if (mChecker.x == 0) {
            mChecker.shouldResetSwiper = true;
        }
        if (mChecker.x < 0) {
            mChecker.x = 0;
        }
        if (mChecker.x > getMenuView().getWidth()) {
            mChecker.x = getMenuView().getWidth();
        }
        return mChecker;
    }

    @Override
    public boolean isClickOnContentView(View contentView, float x) {
        return x < (contentView.getWidth() - getMenuView().getWidth());
    }
}
