package com.park.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.park.lite_patch.Fixer;
import com.park.lite_patch.R;

import java.lang.reflect.Method;

public class MyActivity extends Activity {

    private Need2Fix need2Fix = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        findViewById(R.id.fixButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Method need2Fix = Need2Fix.class.getDeclaredMethod("fixme");
                    Method fixed = Need2Fix.class.getDeclaredMethod("fixed");
                    Fixer.fix(need2Fix, fixed);
                } catch (Throwable tr) {
                    throw new RuntimeException(tr);
                }

            }
        });
        findViewById(R.id.testButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                need2Fix = new Need2Fix();
                textView.setText(need2Fix.fixme());
            }
        });
    }
}
