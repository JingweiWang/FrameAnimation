/*
 * Copyright 2017 JingweiWang
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
import android.widget.Button;
import android.widget.ImageView;

import io.github.jingweiwang.frameanimationlib.FrameAnimation;

public class LoopActivity extends AppCompatActivity implements FrameAnimation.FrameAnimationCallBack {
    private final String TAG = getClass().getSimpleName();
    private FrameAnimation frameAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        Button btn_stop = (Button) findViewById(R.id.btn_stop);
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        ImageView iv_frame = (ImageView) findViewById(R.id.iv_frame);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        frameAnimation = new FrameAnimation(this, iv_frame, Ress.BULLET_FRAME_RESS)
                .setDuration(50)
                .setFrameAnimationCallBack(this);
    }

    private void start() {
        if (frameAnimation != null) {
            frameAnimation.start();
        }
    }

    private void stop() {
        if (frameAnimation != null) {
            frameAnimation.stop();
        }
    }

    private void reset() {
        if (frameAnimation != null) {
            frameAnimation.reset();
        }
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
        }
    }
}
