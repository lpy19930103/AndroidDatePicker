package com.lipy.androiddatepicker.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * original site: https://github.com/wangjiegulu/WheelView
 * Created by lipy on 2016/07/18
 */
public class DataPickerWheelView extends ScrollView {

    public static final int TEXT_SIZE = 16;

    public static final int TEXT_COLOR_FOCUS = 0XFF555555;

    public static final int TEXT_COLOR_NORMAL = 0XFFBBBBBB;

    public static final int LINE_COLOR = 0XFFececec;

    public static final int OFF_SET = 2;

    private static final int DELAY = 50;

    private Context context;

    private LinearLayout views;

    private List<String> items = new ArrayList<String>();

    private int offset = OFF_SET; // 偏移量（需要在最前面和最后面补全）

    private int displayItemCount; // 每页显示的数量

    private int selectedIndex = OFF_SET;

    private int initialY;

    private Runnable scrollerTask = new ScrollerTask();

    private int itemHeight = 0;

    private int[] selectedAreaBorder;//获取选中区域的边界

    private OnWheelViewListener onWheelViewListener;

    private Paint paint;

    private int viewWidth;

    private int textSize = TEXT_SIZE;

    private int textColorNormal = TEXT_COLOR_NORMAL;

    private int textColorFocus = TEXT_COLOR_FOCUS;

    private int lineColor = LINE_COLOR;

    private boolean lineVisible = true;

    private boolean isUserScroll = false;//是否用户手动滚动

    private float previousY = 0;//记录按下时的Y坐标

    /**
     * Instantiates a new Wheel view.
     *
     * @param context the context
     */
    public DataPickerWheelView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Instantiates a new Wheel view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public DataPickerWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Instantiates a new Wheel view.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public DataPickerWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        //  去掉ScrollView的阴影
        setFadingEdgeLength(0);
        if (Build.VERSION.SDK_INT >= 9) {
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

        //取消滚动条
        setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        addView(views);
    }

    /**
     * 手抬起时计算滚动位置
     */
    private void startScrollerTask() {
        initialY = getScrollY();
        postDelayed(scrollerTask, DELAY);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;

        // 添加此句才可以支持联动效果
        views.removeAllViews();

        for (String item : items) {
            views.addView(createView(item));
        }

        // 焦点文字颜色高亮位置，逆推“int position = y / itemHeight + offset”
        refreshItemView(itemHeight * (selectedIndex - offset));
    }

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setText(item);
        tv.setTextSize(textSize);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(8);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }


    /**
     * 刷新textview状态
     */
    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;//余数
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            // 2015/12/15 可设置颜色
            if (position == i) {
                itemView.setTextColor(textColorFocus);
            } else {
                itemView.setTextColor(textColorNormal);
            }
        }
    }

    /**
     * 线的Y坐标
     */
    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    /**
     * 选中回调
     */
    private void onSelectedCallBack() {
        if (null != onWheelViewListener) {
            //  真实的index应该忽略偏移量
            onWheelViewListener.onSelected(isUserScroll, selectedIndex - offset, items.get(selectedIndex));
        }
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }

        // 2015/12/22 可设置分隔线是否可见
        if (!lineVisible) {
            return;
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(lineColor);
            paint.setStrokeWidth(dip2px(1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                int[] areaBorder = obtainSelectedAreaBorder();
//                canvas.drawLine(viewWidth / 6, areaBorder[0], viewWidth * 5 / 6, areaBorder[0], paint);
//                canvas.drawLine(viewWidth / 6, areaBorder[1], viewWidth * 5 / 6, areaBorder[1], paint);
                canvas.drawLine(0, areaBorder[0], viewWidth, areaBorder[0], paint);
                canvas.drawLine(0, areaBorder[1], viewWidth, areaBorder[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };
        super.setBackgroundDrawable(background);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    @Override
    public void fling(int velocityY) {//快滑 当滑动屏幕时Y方向初速度，以每秒像素数计算
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                float delta = ev.getY() - previousY;
                if (selectedIndex == offset && delta > 0) {
                    //滑动到第一项时，若继续向上滑动，则自动跳到最后一项
                    setSelectedIndex(items.size() - offset * 2 - 1);
                } else if (selectedIndex == items.size() - offset - 1 && delta < 0) {
                    //滑动到最后一项时，若继续向下滑动，则自动跳到第一项
                    setSelectedIndex(0);
                } else {
                    isUserScroll = true;
                    startScrollerTask();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void _setItems(List<String> list) {
        items.clear();
        items.addAll(list);

        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }

        initData();

    }

    /**
     * Sets items.
     *
     * @param list the list
     */
    public void setItems(List<String> list) {
        _setItems(list);
        // 初始化时设置默认选中项
        setSelectedIndex(0);
    }

    /**
     * Sets items.
     *
     * @param list  the list
     * @param index the index
     */
    public void setItems(List<String> list, int index) {
        _setItems(list);
        setSelectedIndex(index);
    }

    /**
     * Sets items.
     *
     * @param list the list
     * @param item the item
     */
    public void setItems(List<String> list, String item) {
        _setItems(list);
        setSelectedItem(item);
    }

    /**
     * Gets text size.
     *
     * @return the text size
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * Sets text size.
     *
     * @param textSize the text size
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * Gets text color.
     *
     * @return the text color
     */
    public int getTextColor() {
        return textColorFocus;
    }

    /**
     * Sets text color.
     *
     * @param textColorNormal the text color normal
     * @param textColorFocus  the text color focus
     */
    public void setTextColor( int textColorNormal, int textColorFocus) {
        this.textColorNormal = textColorNormal;
        this.textColorFocus = textColorFocus;
    }

    /**
     * Sets text color.
     *
     * @param textColor the text color
     */
    public void setTextColor(int textColor) {
        this.textColorFocus = textColor;
    }

    /**
     * Is line visible boolean.
     *
     * @return the boolean
     */
    public boolean isLineVisible() {
        return lineVisible;
    }

    /**
     * Sets line visible.
     *
     * @param lineVisible the line visible
     */
    public void setLineVisible(boolean lineVisible) {
        this.lineVisible = lineVisible;
    }

    /**
     * Gets line color.
     *
     * @return the line color
     */
    public int getLineColor() {
        return lineColor;
    }

    /**
     * Sets line color.
     *
     * @param lineColor the line color
     */
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * Gets offset.
     *
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets offset.
     *
     * @param offset the offset
     */
    public void setOffset( int offset) {
        if (offset < 1 || offset > 4) {
            throw new IllegalArgumentException("Offset must between 1 and 4");
        }
        this.offset = offset;
    }

    /**
     * 从0开始计数，所有项包括偏移量
     */
    private void setSelectedIndex( final int index) {
        isUserScroll = false;
        this.post(new Runnable() {
            @Override
            public void run() {
                //滚动到选中项的位置
                smoothScrollTo(0, index * itemHeight);
                //选中这一项的值
                selectedIndex = index + offset;
                onSelectedCallBack();
            }
        });
    }

    /**
     * Sets selected item.
     *
     * @param item the item
     */
    public void setSelectedItem(String item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                //调用_setItems(List)时额外添加了offset个占位符到items里，需要忽略占位符所占的位
                setSelectedIndex(i - offset);
                break;
            }
        }
    }

    /**
     * Use {@link #getSelectedItem()} instead
     */
    @Deprecated
    public String getSeletedItem() {
        return getSelectedItem();
    }

    /**
     * Gets selected item.
     *
     * @return the selected item
     */
    public String getSelectedItem() {
        return items.get(selectedIndex);
    }

    /**
     * Use {@link #getSelectedIndex()} instead
     */
    @Deprecated
    public int getSeletedIndex() {
        return getSelectedIndex();
    }

    /**
     * Gets selected index.
     *
     * @return the selected index
     */
    public int getSelectedIndex() {
        return selectedIndex - offset;
    }

    /**
     * Sets on wheel view listener.
     *
     * @param onWheelViewListener the on wheel view listener
     */
    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    /**
     * The interface On wheel view listener.
     */
    public interface OnWheelViewListener {
        /**
         * On selected.
         *
         * @param isUserScroll  the is user scroll
         * @param selectedIndex the selected index
         * @param item          the item
         */
        void onSelected(boolean isUserScroll, int selectedIndex, String item);
    }

    /**
     * 滚动处理
     */
    private class ScrollerTask implements Runnable {

        @Override
        public void run() {
            // java.lang.ArithmeticException: divide by zero
            if (itemHeight == 0) {

                return;
            }
            int newY = getScrollY();
            if (initialY - newY == 0) { // stopped
                final int remainder = initialY % itemHeight;
                final int divided = initialY / itemHeight;
                if (remainder == 0) {
                    selectedIndex = divided + offset;
                    onSelectedCallBack();
                } else {
                    if (remainder > itemHeight / 2) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                smoothScrollTo(0, initialY - remainder + itemHeight);
                                selectedIndex = divided + offset + 1;
                                onSelectedCallBack();
                            }
                        });
                    } else {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                smoothScrollTo(0, initialY - remainder);
                                selectedIndex = divided + offset;
                                onSelectedCallBack();
                            }
                        });
                    }
                }
            } else {
                startScrollerTask();
            }
        }

    }

}
