/*
 * Copyright (C) 2020 Jinhaihan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hankim.layout

import android.view.View

interface OnRevealChangeListener {
    /**
     * Start Change view
     * You can init view on this function
     * @param fabCircleRevealLayout view
     * @param viewPosition first or second view. 0 or 1.
     */
    fun onViewStartChanged(fabCircleRevealLayout: FabCircleRevealLayout?, viewPosition: Int)

    /**
     * When show first view Animation done, this function will be called
     * @param fabCircleRevealLayout view
     * @param mainView  first view
     */
    fun onMainViewAppeared(fabCircleRevealLayout: FabCircleRevealLayout?, mainView: View?)

    /**
     * When show second view Animation done, this function will be called
     * @param fabCircleRevealLayout view
     * @param secondaryView second view
     */
    fun onSecondaryViewAppeared(fabCircleRevealLayout: FabCircleRevealLayout?, secondaryView: View?)
}