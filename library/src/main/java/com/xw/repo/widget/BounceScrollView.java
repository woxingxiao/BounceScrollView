package com.xw.repo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

public class BounceScrollView extends NestedScrollView {

    private static final float DEFAULT_DAMPING_COEFFICIENT = 4.0f;
    private static final int DEFAULT_SCROLL_THRESHOLD = 20;
    private static final long DEFAULT_BOUNCE_DELAY = 400;

    private boolean mHorizontal;
    private float mDamping;
    private boolean mIncrementalDamping;
    private long mBounceDelay;
    private int mTriggerOverScrollThreshold;

    private Interpolator mInterpolator;
    private View mChildView;
    private float mStart;
    private int mPreDelta;
    private int mOverScrolledDistance;
    private Rect mNormalRect = new Rect();
    private OnOverScrollListener mListener;

    public BounceScrollView(@NonNull Context context) {
        this(context, null);
    }

    public BounceScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BounceScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOverScrollMode(OVER_SCROLL_NEVER);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BounceScrollView, 0, 0);
        mDamping = a.getFloat(R.styleable.BounceScrollView_damping, DEFAULT_DAMPING_COEFFICIENT);
        int orientation = a.getInt(R.styleable.BounceScrollView_scrollOrientation, 0);
        mHorizontal = orientation == 1;
        mIncrementalDamping = a.getBoolean(R.styleable.BounceScrollView_incrementalDamping, true);
        mBounceDelay = a.getInt(R.styleable.BounceScrollView_bounceDelay, (int) DEFAULT_BOUNCE_DELAY);
        mTriggerOverScrollThreshold = a.getInt(R.styleable.BounceScrollView_triggerOverScrollThreshold, DEFAULT_SCROLL_THRESHOLD);
        a.recycle();

        if (mIncrementalDamping) {
            mInterpolator = new DefaultQuartOutInterpolator();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() > 0) {
            mChildView = getChildAt(0);
        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return !mHorizontal;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return mHorizontal;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStart = mHorizontal ? ev.getX() : ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (mHorizontal) {
                    float scrollX = ev.getX() - mStart;
                    return Math.abs(scrollX) >= mTriggerOverScrollThreshold;
                } else {
                    float scrollY = ev.getY() - mStart;
                    return Math.abs(scrollY) >= mTriggerOverScrollThreshold;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mChildView == null)
            return super.onTouchEvent(ev);

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                performClick();

                break;
            case MotionEvent.ACTION_MOVE:
                float now, delta;
                int dampingDelta;

                now = mHorizontal ? ev.getX() : ev.getY();
                delta = mStart - now;
                dampingDelta = (int) (delta / calculateDamping());
                mStart = now;

                boolean onePointerTouch = true;
                if (mPreDelta <= 0 && dampingDelta > 0) {
                    onePointerTouch = false;
                } else if (mPreDelta >= 0 && dampingDelta < 0) {
                    onePointerTouch = false;
                }
                mPreDelta = dampingDelta;

                if (onePointerTouch && canMove(dampingDelta)) {
                    if (mNormalRect.isEmpty()) {
                        mNormalRect.set(mChildView.getLeft(), mChildView.getTop(),
                                mChildView.getRight(), mChildView.getBottom());
                    }

                    if (mHorizontal) {
                        mChildView.layout(mChildView.getLeft() - dampingDelta, mChildView.getTop(),
                                mChildView.getRight() - dampingDelta, mChildView.getBottom());
                    } else {
                        mChildView.layout(mChildView.getLeft(), mChildView.getTop() - dampingDelta,
                                mChildView.getRight(), mChildView.getBottom() - dampingDelta);
                    }

                    if (mListener != null) {
                        mOverScrolledDistance += dampingDelta;
                        mListener.onOverScrolling(mOverScrolledDistance <= 0, Math.abs(mOverScrolledDistance));
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!mNormalRect.isEmpty()) {
                    resetChildViewWithAnimation();
                }
                mPreDelta = 0;
                mOverScrolledDistance = 0;

                break;
        }

        return super.onTouchEvent(ev);
    }

    private float calculateDamping() {
        float ratio;
        if (mHorizontal) {
            ratio = Math.abs(mChildView.getLeft()) * 1.0f / mChildView.getMeasuredWidth();
        } else {
            ratio = Math.abs(mChildView.getTop()) * 1.0f / mChildView.getMeasuredHeight();
        }
        ratio += 0.2;

        if (mIncrementalDamping) {
            return mDamping / (1.0f - (float) Math.pow(ratio, 2));
        } else {
            return mDamping;
        }
    }

    private void resetChildViewWithAnimation() {
        TranslateAnimation anim;
        if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            if (mHorizontal) {
                anim = new TranslateAnimation(mChildView.getPaddingRight(), mNormalRect.right - getPaddingRight(),
                        0, 0);
            } else {
                anim = new TranslateAnimation(0, 0, mChildView.getTop(),
                        mNormalRect.top - getPaddingTop());
            }
        } else {
            if (mHorizontal) {
                anim = new TranslateAnimation(mChildView.getLeft(), mNormalRect.left - getPaddingLeft(),
                        0, 0);
            } else {
                anim = new TranslateAnimation(0, 0, mChildView.getTop(),
                        mNormalRect.top - getPaddingTop());
            }
        }
        anim.setInterpolator(mInterpolator);
        anim.setDuration(mBounceDelay);
        mChildView.startAnimation(anim);
        mChildView.layout(mNormalRect.left, mNormalRect.top, mNormalRect.right, mNormalRect.bottom);

        mNormalRect.setEmpty();
    }

    private boolean canMove(int delta) {
        return delta != 0 && delta < 0 ? canMoveFromStart() : canMoveFromEnd();
    }

    private boolean canMoveFromStart() {
        return mHorizontal ? getScrollX() == 0 : getScrollY() == 0;
    }

    private boolean canMoveFromEnd() {
        if (mHorizontal) {
            int offset = mChildView.getMeasuredWidth() - getWidth();
            offset = offset < 0 ? 0 : offset;
            return getScrollX() == offset;
        } else {
            int offset = mChildView.getMeasuredHeight() - getHeight();
            offset = offset < 0 ? 0 : offset;
            return getScrollY() == offset;
        }
    }

    public void setScrollHorizontally(boolean horizontal) {
        this.mHorizontal = horizontal;
    }

    public boolean isScrollHorizontally() {
        return mHorizontal;
    }

    public float getDamping() {
        return mDamping;
    }

    public void setDamping(@FloatRange(from = 0, to = 100) float damping) {
        if (mDamping > 0) {
            mDamping = damping;
        }
    }

    public long getBounceDelay() {
        return mBounceDelay;
    }

    public void setBounceDelay(long bounceDelay) {
        if (bounceDelay >= 0) {
            mBounceDelay = bounceDelay;
        }
    }

    public void setIncrementalDamping(boolean incrementalDamping) {
        mIncrementalDamping = incrementalDamping;
    }

    public boolean isIncrementalDamping() {
        return mIncrementalDamping;
    }

    public void setBounceInterpolator(@NonNull Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public int getTriggerOverScrollThreshold() {
        return mTriggerOverScrollThreshold;
    }

    public void setTriggerOverScrollThreshold(int threshold) {
        if (threshold >= 0) {
            mTriggerOverScrollThreshold = threshold;
        }
    }

    public void setOnOverScrollListener(OnOverScrollListener listener) {
        mListener = listener;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static class DefaultQuartOutInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return (float) (1.0f - Math.pow(1 - input, 4));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface OnOverScrollListener {
        void onOverScrolling(boolean fromStart, int overScrolledDistance);
    }
}