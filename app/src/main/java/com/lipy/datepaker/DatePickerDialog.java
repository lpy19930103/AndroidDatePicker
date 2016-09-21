package com.lipy.datepaker;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.lipy.androiddatepicker.library.DatePicker;

import java.text.SimpleDateFormat;

/**
 * 日期选择器弹框
 * Created by lipy on 2016/07/19
 */
public class DatePickerDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private DatePicker datePicker;
    private PositiveBtnClick mPositiveBtnClick;

    public DatePickerDialog(Context context, PositiveBtnClick positiveBtnClick) {
        this(context, R.style.common_dialog);
        mPositiveBtnClick = positiveBtnClick;
    }

    public DatePickerDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        init();

    }

    protected DatePickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View view = View.inflate(mContext, R.layout.datepicker_dialog_view, null);
        view.findViewById(R.id.heelview_dialog_negative_btn).setOnClickListener(this);
        view.findViewById(R.id.heelview_dialog_positive_btn).setOnClickListener(this);
        datePicker = (DatePicker) view.findViewById(R.id.wheel_view_wv);
        try {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
            String[] split = date.split("-");
            int i = Integer.parseInt(split[0]);
            datePicker.setRange(2000, i + 20);
            datePicker.setSelectedItem(Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(view);
    }

    /**
     * 设置弹出后展示的年月日,必须填写当前时间往后推20年的数字
     * @param year
     * @param mouth
     * @param day
     */
    public void setSelectedDate(int year, int mouth, int day) {
        datePicker.setSelectedItem(year, mouth, day);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.heelview_dialog_negative_btn) {
            dismiss();
        } else if (v.getId() == R.id.heelview_dialog_positive_btn) {
            datePicker.setOnDatePickListener(new DatePicker.OnPickListener() {
                @Override
                public void onDatePicked(String year, String month, String day) {
                    if (mPositiveBtnClick != null) {
                        dismiss();
                        mPositiveBtnClick.onPositiveBtnClick(year, month, day);
                    }
                }
            });
        }
    }

    public interface PositiveBtnClick {
        void onPositiveBtnClick(String year, String month, String day);
    }
}
