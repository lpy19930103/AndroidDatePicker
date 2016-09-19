package com.lipy.datepaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dateDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.PositiveBtnClick() {
                    @Override
                    public void onPositiveBtnClick(String year, String month, String day) {
                        Toast.makeText(MainActivity.this, year + month + day, Toast.LENGTH_SHORT).show();
                    }
                });
                dateDialog.show();
            }
        });
    }
}
