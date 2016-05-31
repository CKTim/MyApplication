package widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by cxk on 2016/4/9.
 */
public class Custom_comment_listview extends ListView {
    public Custom_comment_listview(Context context) {
        super(context);
    }

    public Custom_comment_listview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Custom_comment_listview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
