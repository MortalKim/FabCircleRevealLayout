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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.RelativeLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class FabCircleRevealLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_CHILD_VIEWS = 2
    }

    private var FAB_SIZE = 48
    private var ANIMATION_DURATION = 500
    private val INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()
    private var isShowingMainView = true
    private var ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.FabCircleRevealLayout)
    var originalX = 0f
    var originalY = 0f
    var mode = 0
    private var childViews: MutableList<View>? = null
    private var fab: FloatingActionButton? = null
    private var onRevealChangeListener: OnRevealChangeListener? = null
    private val fabClickListener = OnClickListener { revealSecondaryView() }

    init {
        childViews = ArrayList(2)
        ANIMATION_DURATION = ta.getInt(R.styleable.FabCircleRevealLayout_animationDuration,500)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        setupView(child)
        super.addView(child, index, params)
        if (areAllComponentsReady()) {
            setupInitialState()
        }
    }

    private fun setupView(child: View) {
        if (child is FloatingActionButton) {
            setupFAB(child)
        } else {
            setupChildView(child)
        }
    }

    private fun setupFAB(view: View) {
        validateFAB()
        fab = view as FloatingActionButton
        fab!!.setOnClickListener(fabClickListener)
    }

    private fun setupChildView(view: View) {
        validateChildView()
        childViews!!.add(view)
    }

    private fun validateFAB() {
        require(fab == null) { "FABRevealLayout can only hold one FloatingActionButton" }
    }

    private fun validateChildView() {
        require(childViews!!.size < MAX_CHILD_VIEWS) { "FABRevealLayout can only hold two views" }
    }

    private fun areAllComponentsReady(): Boolean {
        return fab != null && childViews!!.size == MAX_CHILD_VIEWS
    }

    private fun setupInitialState() {
        setupFABPosition()
        setupChildViewsPosition()
    }

    private fun setupFABPosition() {
        val params = fab!!.layoutParams as LayoutParams
        mode = ta.getInt(R.styleable.FabCircleRevealLayout_fabMode, 0)
        if (mode == 0) {
            params.addRule(ALIGN_PARENT_TOP, TRUE)
            params.addRule(ALIGN_PARENT_RIGHT, TRUE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                params.rightMargin = dipsToPixels(16f)
                params.topMargin = dipsToPixels(20f)
            }
        } else {
            val x = ta.getDimension(R.styleable.FabCircleRevealLayout_fabX, 0f)
            val y = ta.getDimension(R.styleable.FabCircleRevealLayout_fabY, 0f)
            params.leftMargin = x.toInt()
            params.topMargin = y.toInt()
        }
        fab!!.bringToFront()

        //run when view layout finished
        post {
            originalX = fab!!.x
            originalY = fab!!.y
        }
    }

    private fun setupChildViewsPosition() {
        for (i in childViews!!.indices) {
            val params = childViews!![i].layoutParams as LayoutParams
            params.topMargin = dipsToPixels(FAB_SIZE.toFloat())
        }
        secondaryView.visibility = View.GONE
    }

    fun revealMainView() {
        startHideAnimation()
    }

    fun revealSecondaryView() {
        startRevealAnimation()
    }

    fun setOnRevealChangeListener(onRevealChangeListener: OnRevealChangeListener?) {
        this.onRevealChangeListener = onRevealChangeListener
    }

    private fun startRevealAnimation() {
        //View disappearingView = getMainView();
        val fabAnimator = fABAnimator
        //ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(disappearingView, "alpha", 1, 0);
        val set = AnimatorSet()
        set.play(fabAnimator) //.with(alphaAnimator);
        setupAnimationParams(set)
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                fab!!.visibility = View.GONE
                expandCircle()
            }
        })
        set.start()
    }

    private fun setupAnimationParams(animator: Animator) {
        animator.interpolator = INTERPOLATOR
        animator.duration = ANIMATION_DURATION.toLong()
    }

    private val curvedAnimator: CurvedAnimator
        private get() {
            val view = mainView
            val fromX = originalX
            val fromY = originalY
            val toX = view.width / 2 - fab!!.width / 2 + view.left.toFloat()
            val toY = view.height / 2 - fab!!.height / 2 + view.top.toFloat()
            return if (isShowingMainView) {
                CurvedAnimator(fromX, fromY, toX, toY)
            } else {
                CurvedAnimator(toX, toY, fromX, fromY)
            }
        }

    private val fABAnimator: ObjectAnimator
        private get() {
            val curvedAnimator = curvedAnimator
            return ObjectAnimator.ofObject(this, "fabPositionInAnim", CurvedPathEvaluator(), *curvedAnimator.getPoints())
        }

    private fun expandCircle() {
        secondaryView.visibility = View.VISIBLE
        if (onRevealChangeListener != null) {
            onRevealChangeListener!!.onViewStartChanged(this, 1)
        }
        val centerX = (fab!!.x + fab!!.width / 2).toInt()
        val centerY = (fab!!.y - fab!!.height / 2).toInt()
        val finalRadius = Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()
        val mCircularReveal = ViewAnimationUtils.createCircularReveal(
                secondaryView, centerX, centerY, Math.sqrt(fab!!.width.toDouble()).toFloat(), finalRadius)
        mCircularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isShowingMainView = false
                notifyListener()
            }
        })
        mCircularReveal.setDuration(ANIMATION_DURATION.toLong()).start()
    }

    private fun startHideAnimation() {
        if (onRevealChangeListener != null) {
            onRevealChangeListener!!.onViewStartChanged(this, 0)
        }
        val centerX = (fab!!.x + fab!!.width / 2).toInt()
        val centerY = (fab!!.y - fab!!.height / 2).toInt()
        val finalRadius = Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()
        val mCircularReveal = ViewAnimationUtils.createCircularReveal(
                secondaryView, centerX, centerY, finalRadius, Math.sqrt(fab!!.width.toDouble()).toFloat())
        mCircularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                secondaryView.visibility = View.GONE
                fab!!.visibility = View.VISIBLE
                moveFABToOriginalLocation()
                isShowingMainView = true
                notifyListener()
            }
        })
        mCircularReveal.setDuration(ANIMATION_DURATION.toLong()).start()
    }

    private fun moveFABToOriginalLocation() {
        val fabAnimator = fABAnimator
        setupAnimationParams(fabAnimator)
        fabAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }
        })
        fabAnimator.start()
    }

    /**
     * Set Fab button Position
     * @param point
     */
    fun setFabPosition(point: Point) {
        originalX = point.x
        originalY = point.y
        fab!!.x = point.x
        fab!!.y = point.y
        fab!!.bringToFront()
    }

    /**
     * Use by ObjectAnimator.ofObject. NOT call it.
     * @param point
     */
    fun setFabPositionInAnim(point: Point) {
        fab!!.x = point.x
        fab!!.y = point.y
    }

    private fun notifyListener() {
        if (onRevealChangeListener != null) {
            if (isShowingMainView) {
                onRevealChangeListener!!.onMainViewAppeared(this, mainView)
            } else {
                onRevealChangeListener!!.onSecondaryViewAppeared(this, secondaryView)
            }
        }
    }

    private val secondaryView: View
        private get() = childViews!![1]

    private val mainView: View
        private get() = childViews!![0]

    private fun dipsToPixels(dips: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, resources.displayMetrics).toInt()
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        (params as MarginLayoutParams).topMargin -= dipsToPixels(FAB_SIZE.toFloat())
        super.setLayoutParams(params)
    }

   fun setAnimationDuration(time:Int){
       ANIMATION_DURATION = time
   }

}