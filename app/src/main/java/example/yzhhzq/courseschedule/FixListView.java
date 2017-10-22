package example.yzhhzq.courseschedule;

import android.widget.ListView;
import android.content.Context;
import android.util.AttributeSet;
/**
 * Created by hasee on 2016/10/24.
 */

public class FixListView extends ListView {
    public  FixListView(Context context) {
        super(context);
    }

    public  FixListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * fix the scroll
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}