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
package com.hankim.fabreveallayoutsample;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.hankim.layout.FabCircleRevealLayout;
import com.hankim.layout.OnRevealChangeListener;

public class MainActivity extends AppCompatActivity {

    private FabCircleRevealLayout fabCircleRevealLayout;
    private SeekBar songProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        configureFABReveal();
    }

    private void findViews() {
        fabCircleRevealLayout = (FabCircleRevealLayout) findViewById(R.id.fab_reveal_layout);
        songProgress = (SeekBar) findViewById(R.id.song_progress_bar);
        styleSeekbar(songProgress);
    }

    private void styleSeekbar(SeekBar songProgress) {
        int color = getResources().getColor(R.color.background);
        songProgress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        songProgress.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void configureFABReveal() {
        fabCircleRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onViewStartChanged(FabCircleRevealLayout fabCircleRevealLayout, int viewPosition) {

            }

            @Override
            public void onMainViewAppeared(FabCircleRevealLayout fabCircleRevealLayout, View mainView) {

            }

            @Override
            public void onSecondaryViewAppeared(final FabCircleRevealLayout fabCircleRevealLayout, View secondaryView) {
                prepareBackTransition(fabCircleRevealLayout);
            }
        });
    }

    private void prepareBackTransition(final FabCircleRevealLayout fabCircleRevealLayout) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabCircleRevealLayout.revealMainView();
            }
        }, 3000);
    }

}
