package customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.RSMSA.policeApp.R;

/**
 *  Created by Isaiah on 7/20/2014.
 */
public class TouchFeedbackEnabledRelativeLayout extends RelativeLayout {

    public TouchFeedbackEnabledRelativeLayout(Context context) {
        super(context);
    }

    public TouchFeedbackEnabledRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchFeedbackEnabledRelativeLayout(Context context, AttributeSet attrs,
                                              int defStyle) {
        super(context, attrs, defStyle);
    }

    private Drawable touchFeedbackDrawable;

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();

        touchFeedbackDrawable = getResources().getDrawable(R.drawable.touch_selector);
    }

    @Override
    protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
        touchFeedbackDrawable.setBounds(0, 0, getWidth(), getHeight());
        touchFeedbackDrawable.draw(canvas);

    }

    @Override
    protected void drawableStateChanged() {
        if (touchFeedbackDrawable != null) {
            touchFeedbackDrawable.setState(getDrawableState());
            invalidate();
        }
        super.drawableStateChanged();
    }


}