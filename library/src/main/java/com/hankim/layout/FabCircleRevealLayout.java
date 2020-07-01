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
package com.hankim.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class FabCircleRevealLayout extends RelativeLayout {

    private static final int MAX_CHILD_VIEWS = 2;
    private static final int FAB_SIZE = 48;
    private static final int ANIMATION_DURATION = 500;
    private final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean isShowingMainView = true;
    TypedArray ta;
    float originalX = 0;
    float originalY = 0;
    int mode = 0;

    private List<View> childViews = null;
    private FloatingActionButton fab = null;
    private OnRevealChangeListener onRevealChangeListener = null;
    private OnClickListener fabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            revealSecondaryView();
        }
    };

    public FabCircleRevealLayout(Context context) {
        this(context, null);
    }

    public FabCircleRevealLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FabCircleRevealLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ta = context.obtainStyledAttributes(attrs, R.styleable.FabCircleRevealLayout);
        childViews = new ArrayList<>(2);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        setupView(child);
        super.addView(child, index, params);

        if(areAllComponentsReady()){
            setupInitialState();
        }
    }

    private void setupView(View child) {
        if(child instanceof FloatingActionButton){
            setupFAB(child);
        }else{
            setupChildView(child);
        }
    }

    private void setupFAB(View view){
        validateFAB();
        fab = (FloatingActionButton) view;
        fab.setOnClickListener(fabClickListener);
    }

    private void setupChildView(View view){
        validateChildView();
        childViews.add(view);
    }

    private void validateFAB() {
        if(fab != null){
            throw new IllegalArgumentException("FABRevealLayout can only hold one FloatingActionButton");
        }
    }

    private void validateChildView() {
        if(childViews.size() >= MAX_CHILD_VIEWS){
            throw new IllegalArgumentException("FABRevealLayout can only hold two views");
        }
    }


    private boolean areAllComponentsReady(){
        return fab != null && childViews.size() == MAX_CHILD_VIEWS;
    }

    private void setupInitialState(){
        setupFABPosition();
        setupChildViewsPosition();
    }

    private void setupFABPosition(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fab.getLayoutParams();
        mode = ta.getInt(R.styleable.FabCircleRevealLayout_fabMode, 0 );
        if(mode == 0){
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                params.rightMargin = dipsToPixels(16);
                params.topMargin = dipsToPixels(20);
            }
        }
        else{
            float x = ta.getDimension(R.styleable.FabCircleRevealLayout_fabX, 0 );
            float y = ta.getDimension(R.styleable.FabCircleRevealLayout_fabY, 0 );
            params.leftMargin = (int)x;
            params.topMargin = (int)y;
        }

        fab.bringToFront();

        //run when view layout finished
        post(new Runnable() {
            @Override
            public void run() {
                originalX = fab.getX();
                originalY = fab.getY();
            }
        });
    }

    private void setupChildViewsPosition(){
        for(int i = 0; i < childViews.size(); i++){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) childViews.get(i).getLayoutParams();
            params.topMargin = dipsToPixels(FAB_SIZE);
        }
        getSecondaryView().setVisibility(GONE);
    }



    public void revealMainView(){
        startHideAnimation();
    }

    public void revealSecondaryView(){
        startRevealAnimation();
    }

    public void setOnRevealChangeListener(OnRevealChangeListener onRevealChangeListener) {
        this.onRevealChangeListener = onRevealChangeListener;
    }

    private void startRevealAnimation(){
        //View disappearingView = getMainView();

        ObjectAnimator fabAnimator = getFABAnimator();
        //ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(disappearingView, "alpha", 1, 0);

        AnimatorSet set = new AnimatorSet();
        set.play(fabAnimator);//.with(alphaAnimator);
        setupAnimationParams(set);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab.setVisibility(GONE);
                expandCircle();
            }
        } );

        set.start();
    }


    private void setupAnimationParams(Animator animator) {
        animator.setInterpolator(INTERPOLATOR);
        animator.setDuration(ANIMATION_DURATION);
    }

    private CurvedAnimator getCurvedAnimator() {
        View view = getMainView();

        float fromX = originalX;
        float fromY = originalY;
        float toX = view.getWidth() / 2 - fab.getWidth() / 2 + view.getLeft();
        float toY = view.getHeight() / 2 - fab.getHeight() / 2 + view.getTop();

        if(isShowingMainView) {
            return new CurvedAnimator(fromX, fromY, toX, toY);
        }else{
            return new CurvedAnimator(toX, toY, fromX, fromY);
        }
    }

    private ObjectAnimator getFABAnimator(){
        CurvedAnimator curvedAnimator = getCurvedAnimator();
        return ObjectAnimator.ofObject(this, "fabPositionInAnim", new CurvedPathEvaluator(), curvedAnimator.getPoints());
    }

    private void expandCircle(){
        getSecondaryView().setVisibility(VISIBLE);
        if(onRevealChangeListener != null ){
            onRevealChangeListener.onViewStartChanged(this,1);
        }
        int centerX = (int)(fab.getX() + fab.getWidth()/2);
        int centerY = (int)(fab.getY() - fab.getHeight()/2);

        float finalRadius = (float) Math.hypot((double) centerX, (double) centerY);

        Animator mCircularReveal = ViewAnimationUtils.createCircularReveal(
                getSecondaryView(), centerX, centerY, (float) Math.sqrt(fab.getWidth()), finalRadius);

        mCircularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShowingMainView =false;
                notifyListener();
            }
        });

        mCircularReveal.setDuration(ANIMATION_DURATION).start();

    }

    private void startHideAnimation(){
        if(onRevealChangeListener != null ){
            onRevealChangeListener.onViewStartChanged(this,0);
        }
        int centerX = (int)(fab.getX() + fab.getWidth()/2);
        int centerY = (int)(fab.getY() - fab.getHeight()/2);
        float finalRadius = (float) Math.hypot((double) centerX, (double) centerY);
        Animator mCircularReveal = ViewAnimationUtils.createCircularReveal(
                getSecondaryView(), centerX, centerY,finalRadius,(float) Math.sqrt(fab.getWidth()));
        mCircularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                getSecondaryView().setVisibility(GONE);
                fab.setVisibility(VISIBLE);
                moveFABToOriginalLocation();
                isShowingMainView = true;
                notifyListener();
            }
        });
        mCircularReveal.setDuration(ANIMATION_DURATION).start();
    }

    private void moveFABToOriginalLocation(){
        ObjectAnimator fabAnimator = getFABAnimator();

        setupAnimationParams(fabAnimator);
        fabAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        fabAnimator.start();
    }

    /**
     * Set Fab button Position
     * @param point
     */
    public void setFabPosition(Point point){
        originalX = point.x;
        originalY = point.y;
        fab.setX(point.x);
        fab.setY(point.y);
        fab.bringToFront();
    }

    /**
     * Use by ObjectAnimator.ofObject. NOT call it.
     * @param point
     */
    public void setFabPositionInAnim(Point point){
        fab.setX(point.x);
        fab.setY(point.y);
    }



    private void notifyListener(){
        if(onRevealChangeListener != null){
            if(isShowingMainView){
                onRevealChangeListener.onMainViewAppeared(this, getMainView());
            }else{
                onRevealChangeListener.onSecondaryViewAppeared(this, getSecondaryView());
            }
        }
    }

    private View getSecondaryView() {
        return childViews.get(1);
    }

    private View getMainView() {
        return childViews.get(0);
    }

    private int dipsToPixels(float dips){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, getResources().getDisplayMetrics());
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        ((MarginLayoutParams) params).topMargin -= dipsToPixels(FAB_SIZE);
        super.setLayoutParams(params);
    }

}
