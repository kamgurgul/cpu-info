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
public abstract class Swiper {

    protected static final int BEGIN_DIRECTION = 1;
    protected static final int END_DIRECTION = -1;

    private int direction;
    private View menuView;
    protected Checker mChecker;

    public Swiper(int direction, View menuView) {
        this.direction = direction;
        this.menuView = menuView;
        mChecker = new Checker();
    }

    public abstract boolean isMenuOpen(final int scrollDis);

    public abstract boolean isMenuOpenNotEqual(final int scrollDis);

    public abstract void autoOpenMenu(OverScroller scroller, int scrollDis, int duration);

    public abstract void autoCloseMenu(OverScroller scroller, int scrollDis, int duration);

    public abstract Checker checkXY(int x, int y);

    public abstract boolean isClickOnContentView(View contentView, float clickPoint);

    public int getDirection() {
        return direction;
    }

    public View getMenuView() {
        return menuView;
    }

    public int getMenuWidth() {
        return getMenuView().getWidth();
    }

    public int getMenuHeight() {
        return getMenuView().getHeight();
    }

    public static final class Checker {
        public int x;
        public int y;
        public boolean shouldResetSwiper;
    }

}
