package io.github.jingweiwang.demo;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AndroidNativeFAActivity extends AppCompatActivity {
    private AnimationDrawable frameAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_native_fa);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
        ImageView iv_frame = (ImageView) findViewById(R.id.iv_frame);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            frameAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.material_anim, null);
        } else {
            frameAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.material_anim);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            iv_frame.setBackground(frameAnim);
        } else {
            iv_frame.setBackgroundDrawable(frameAnim);
        }
    }

    protected void start() {
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.start();
        }
    }

    @Override
    protected void onDestroy() {
        if (frameAnim != null && frameAnim.isRunning()) {
            frameAnim.stop();
        }
        super.onDestroy();
    }
}