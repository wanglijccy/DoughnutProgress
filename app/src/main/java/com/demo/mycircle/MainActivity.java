package com.demo.mycircle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private LinearLayout mLinerBody;
    private PercentDrawable mDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLinerBody = (LinearLayout) findViewById(R.id.ll_body);

        mDrawable = new PercentDrawable(this, Color.parseColor("#FF846C"));

        mLinerBody.setBackgroundDrawable(mDrawable);

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawable.startAnimationWithPercent(90);
            }
        });

        findViewById(R.id.second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });

    }
}
