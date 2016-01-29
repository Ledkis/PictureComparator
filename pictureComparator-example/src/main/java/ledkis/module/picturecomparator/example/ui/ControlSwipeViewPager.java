package ledkis.module.picturecomparator.example.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * TODO not used : implementation wip
 * http://stackoverflow.com/questions/12276846/one-side-viewpager-swiping-only
 */
public class ControlSwipeViewPager extends ViewPager {

    public static final String TAG = "ControlSwipeViewPager2";

    private boolean enabled = true;

    public ControlSwipeViewPager(Context context) {
        super(context);
    }

    public ControlSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

