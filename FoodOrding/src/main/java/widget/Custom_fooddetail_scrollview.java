package widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by cxk on 2016/4/8.
 */
public class Custom_fooddetail_scrollview extends ScrollView {
    private OnScrollListener onScrollListener;

    public Custom_fooddetail_scrollview(Context context) {
        super(context);
    }

    public Custom_fooddetail_scrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Custom_fooddetail_scrollview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //定义一个接口
    public interface OnScrollListener {
        public void OnScroll(int y);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null) {
            onScrollListener.OnScroll(t);
        }
    }
}