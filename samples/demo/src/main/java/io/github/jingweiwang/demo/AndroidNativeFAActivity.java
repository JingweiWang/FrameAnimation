/*
 * Copyright 2018 JingweiWang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jingweiwang.demo;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class AndroidNativeFAActivity extends AppCompatActivity {
    private AnimationDrawable frameAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_native_fa);

        ImageView iv_frame = findViewById(R.id.iv_frame);

        findViewById(R.id.btn_start).setOnClickListener(v -> start());

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

    private void start() {
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