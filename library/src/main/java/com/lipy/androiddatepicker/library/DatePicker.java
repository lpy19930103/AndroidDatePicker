package com.lipy.androiddatepicker.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 日期选择器
 * Created by lipy on 2016/07/18
 */
public class DatePicker extends LinearLayout {

    private Context mContext;

    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    private ArrayList<String> years = new ArrayList<>();

    private ArrayList<String> months = new ArrayList<>();

    private ArrayList<String> days = new ArrayList<>();

    private OnPickListener onDatePickListener;

    private String yearLabel = "年", monthLabel = "月", dayLabel = "日";

    private int selectedYearIndex = 0, selectedMonthIndex = 0, selectedDayIndex = 0;

    private DataPickerWheelView mYearView;

    private DataPickerWheelView mDayView;

    private DataPickerWheelView mMonthView;

    public DatePicker(Context context) {
        super(context);
        init(context);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 处理数据
     */
    private void init(Context context) {
        mContext = context;
        for (int i = 2000; i <= 2050; i++) {
            years.add(String.valueOf(i) + yearLabel);
        }
        for (int i = 1; i <= 12; i++) {
            months.add(fillZero(i) + monthLabel);
        }
        for (int i = 1; i <= 31; i++) {
            days.add(fillZero(i) + dayLabel);
        }
        setGravity(Gravity.CENTER);
        addView(makeCenterView());
    }

    /**
     * 月日时分秒，0-9前补0
     */
    public static String fillZero(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    /**
     * 设置单位
     */
    public void setLabel(String yearLabel, String monthLabel, String dayLabel) {
        this.yearLabel = yearLabel;
        this.monthLabel = monthLabel;
        this.dayLabel = dayLabel;
    }

    /**
     * 设置起始年 结束年范围
     */
    public void setRange(int startYear, int endYear) {
        years.clear();
        for (int i = startYear; i <= endYear; i++) {
            years.add(String.valueOf(i) + yearLabel);
        }
    }

    /**
     * 查找传进来的参数的位置
     */
    private int findItemIndex(ArrayList<String> items, int item) {

        int index = Collections.binarySearch(items, item, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String lhsStr = lhs.toString();
                String rhsStr = rhs.toString();
                lhsStr = lhsStr.startsWith("0") ? lhsStr.substring(1) : lhsStr;
                rhsStr = rhsStr.startsWith("0") ? rhsStr.substring(1) : rhsStr;
                if (lhsStr.endsWith(yearLabel) || lhsStr.endsWith(monthLabel) || lhsStr.endsWith(dayLabel)) {
                    lhsStr = lhsStr.substring(0, lhsStr.length() - 1);
                }
                if (rhsStr.endsWith(yearLabel) || rhsStr.endsWith(monthLabel) || rhsStr.endsWith(dayLabel)) {
                    rhsStr = rhsStr.substring(0, rhsStr.length() - 1);
                }
                return Integer.parseInt(lhsStr) - Integer.parseInt(rhsStr);
            }
        });
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    /**
     * 设置年月日位置
     */
    public void setSelectedItem(int year, int month, int day) {
        selectedYearIndex = findItemIndex(years, year);
        selectedMonthIndex = findItemIndex(months, month);
        selectedDayIndex = findItemIndex(days, day);
        refreshDate();
    }

    public void refreshDate() {
        if (selectedMonthIndex == 0) {
            mMonthView.setItems(months);
        } else {
            mMonthView.setItems(months, selectedMonthIndex);
        }
        if (selectedDayIndex == 0) {
            mDayView.setItems(days);
        } else {
            mDayView.setItems(days, selectedDayIndex);
        }
        if (selectedYearIndex == 0) {
            mYearView.setItems(years);
        } else {
            mYearView.setItems(years, selectedYearIndex);
        }
    }

    /**
     * 计算天数
     */
    public static int calculateDaysInMonth(int year, int month) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] bigMonths = {"1", "3", "5", "7", "8", "10", "12"};
        String[] littleMonths = {"4", "6", "9", "11"};
        List<String> bigList = Arrays.asList(bigMonths);
        List<String> littleList = Arrays.asList(littleMonths);
        // 判断大小月及是否闰年,用来确定日的数据
        if (bigList.contains(String.valueOf(month))) {
            return 31;
        } else if (littleList.contains(String.valueOf(month))) {
            return 30;
        } else {
            if (year <= 0) {
                return 29;
            }
            // 是否闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
    }


    /**
     * 设置datePickListener
     *
     * @param listener the listener
     */
    public void setOnDatePickListener(OnPickListener listener) {
        this.onDatePickListener = listener;
        onSubmit();
    }

    /**
     * 设置字体大小
     *
     * @param size
     */
    public void setTextSize(int size) {
        if (mYearView != null) {
            mYearView.setTextSize(size);
        }
        if (mMonthView != null) {
            mMonthView.setTextSize(size);
        }
        if (mDayView != null) {
            mDayView.setTextSize(size);
        }
    }

    /**
     * 设置选中的和未选中的文字颜色
     *
     * @param normalColor
     * @param focusColor
     */
    public void setTextColor(int normalColor, int focusColor) {
        if (mYearView != null) {
            mYearView.setTextColor(normalColor, focusColor);
        }
        if (mMonthView != null) {
            mMonthView.setTextColor(normalColor, focusColor);
        }
        if (mDayView != null) {
            mDayView.setTextColor(normalColor, focusColor);
        }
    }

    /**
     * 是否显示分割线
     *
     * @param visible
     */
    public void setLineVisible(boolean visible) {
        if (mYearView != null) {
            mYearView.setLineVisible(visible);
        }
        if (mMonthView != null) {
            mMonthView.setLineVisible(visible);
        }
        if (mDayView != null) {
            mDayView.setLineVisible(visible);
        }
    }

    /**
     * 分割线颜色
     *
     * @param color
     */
    public void setLineColor(int color) {
        if (mYearView != null) {
            mYearView.setLineColor(color);
        }
        if (mMonthView != null) {
            mMonthView.setLineColor(color);
        }
        if (mDayView != null) {
            mDayView.setLineColor(color);
        }
    }

    /**
     * 上下偏移条目数
     *
     * @param offset
     */
    public void setOffset(int offset) {
        if (mYearView != null) {
            mYearView.setOffset(offset);
        }
        if (mMonthView != null) {
            mMonthView.setOffset(offset);
        }
        if (mDayView != null) {
            mDayView.setOffset(offset);
        }
        invalidate();
    }


    protected View makeCenterView() {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);
        mYearView = new DataPickerWheelView(mContext);
        mYearView.setLayoutParams(new LayoutParams(dp2px(mContext, 120), WRAP_CONTENT));
        mYearView = new DataPickerWheelView(mContext);
        mYearView.setLayoutParams(new LayoutParams(dp2px(mContext, 120), WRAP_CONTENT));
        layout.addView(mYearView);

        mMonthView = new DataPickerWheelView(mContext);
        mMonthView.setLayoutParams(new LayoutParams(dp2px(mContext, 120), WRAP_CONTENT));
        mMonthView.setLayoutParams(new LayoutParams(dp2px(mContext, 120), WRAP_CONTENT));
        layout.addView(mMonthView);

        mDayView = new DataPickerWheelView(mContext);
        mDayView.setLayoutParams(new LayoutParams(dp2px(mContext, 120), WRAP_CONTENT));
        mDayView.setLayoutParams(new LayoutParams(dp2px(mContext, 120), WRAP_CONTENT));
        layout.addView(mDayView);

        if (selectedYearIndex == 0) {
            mYearView.setItems(years);
        } else {
            mYearView.setItems(years, selectedYearIndex);
        }
        mYearView.setOnWheelViewListener(new DataPickerWheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedYearIndex = selectedIndex;
                //需要根据年份及月份动态计算天数
                days.clear();
                int maxDays = calculateDaysInMonth(stringToDateInt(item), stringToDateInt(months.get(selectedMonthIndex)));
                for (int i = 1; i <= maxDays; i++) {
                    days.add(fillZero(i) + dayLabel);
                }
                if (selectedDayIndex >= maxDays) {
                    selectedDayIndex = days.size() - 1;
                }
                mDayView.setItems(days, selectedDayIndex);
            }
        });

        if (selectedMonthIndex == 0) {
            mMonthView.setItems(months);
        } else {
            mMonthView.setItems(months, selectedMonthIndex);
        }
        mMonthView.setOnWheelViewListener(new DataPickerWheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedMonthIndex = selectedIndex;
                days.clear();
                int maxDays = calculateDaysInMonth(stringToDateInt(years.get(selectedYearIndex)), stringToDateInt(item));
                for (int i = 1; i <= maxDays; i++) {
                    days.add(fillZero(i) + dayLabel);
                }
                if (selectedDayIndex >= maxDays) {
                    selectedDayIndex = days.size() - 1;
                }
                mDayView.setItems(days, selectedDayIndex);
            }
        });
        if (selectedDayIndex == 0) {
            mDayView.setItems(days);
        } else {
            mDayView.setItems(days, selectedDayIndex);
        }
        mDayView.setOnWheelViewListener(new DataPickerWheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedDayIndex = selectedIndex;
            }
        });
        return layout;
    }

    private int stringToDateInt(String text) {
        if (text.startsWith("0")) {
            //截取掉前缀0以便转换为整数
            text = text.substring(1);
        }
        if (text.endsWith(yearLabel) || text.endsWith(monthLabel) || text.endsWith(dayLabel)) {
            text = text.substring(0, text.length() - 1);
        }
        return Integer.parseInt(text);
    }

    protected void onSubmit() {
        if (onDatePickListener != null) {
            String year = getSelectedYear();
            String month = getSelectedMonth();
            String day = getSelectedDay();
            onDatePickListener.onDatePicked(year, month, day);
        }
    }

    /**
     * dp 转换成 px
     */
    public static int dp2px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public String getSelectedYear() {
        return years.get(selectedYearIndex);
    }

    public String getSelectedMonth() {
        return months.get(selectedMonthIndex);
    }

    public String getSelectedDay() {
        return days.get(selectedDayIndex);
    }

    public interface OnPickListener {
        void onDatePicked(String year, String month, String day);
    }

}
