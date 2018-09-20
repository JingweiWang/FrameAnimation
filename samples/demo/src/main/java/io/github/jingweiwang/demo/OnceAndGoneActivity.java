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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import io.github.jingweiwang.frameanimationlib.FrameAnimation;

public class OnceAndGoneActivity extends AppCompatActivity implements FrameAnimation.FrameAnimationCallBack {
    private final String TAG = "OnceAndGoneActivity";
    private FrameAnimation frameAnimation;
    private ImageView iv_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_once_and_gone);

        iv_frame = findViewById(R.id.iv_frame);

        findViewById(R.id.btn_start).setOnClickListener(v -> start());

        frameAnimation = new FrameAnimation(this, iv_frame, Ress.MATERIAL_FRAME_RESS)
                .setDuration(50)
                .setOneShot(true)
                .setFrameAnimationCallBack(this)
                .setOpenLog(true);
    }

    private void start() {
        iv_frame.setVisibility(View.VISIBLE);
        frameAnimation.start();
    }

    @Override
    protected void onPause() {
        frameAnimation.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        frameAnimation.clearCache();
        super.onDestroy();
    }

    @Override
    public void onFrameAnimationStart(FrameAnimation frameAnimation) {
        if (this.frameAnimation.equals(frameAnimation)) {
            Log.e(TAG, "onFrameAnimationStart");
        }
    }

    @Override
    public void onFrameAnimationEnd(FrameAnimation frameAnimation) {
        if (this.frameAnimation.equals(frameAnimation)) {
            Log.e(TAG, "onFrameAnimationEnd");
            iv_frame.setVisibility(View.GONE);
        }
    }
}
