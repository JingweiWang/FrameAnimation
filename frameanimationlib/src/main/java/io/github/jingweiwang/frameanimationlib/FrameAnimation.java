/*
 *    Copyright 2017 JingweiWang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.jingweiwang.frameanimationlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

public class FrameAnimation {
    private final int duration = 40;
    private Context context;
    private ImageView imageView;
    private View parentView;
    private Handler handler = new Handler();
    private LruCacheManager<Integer, BitmapDrawable> lruCacheManager;
    private int[] frameRess;
    private int frameCount;
    private boolean loop = false;

    public FrameAnimation(Context context, ImageView imageView, int[] pFrameRess) {
        this.context = context.getApplicationContext();
        this.imageView = imageView;
        this.frameRess = pFrameRess;
        this.frameCount = pFrameRess.length - 1;
        initLruCache();
    }

    public FrameAnimation(Context context, ImageView imageView, @DrawableRes int frameRes_head, int totle) {
        this.context = context.getApplicationContext();
        this.imageView = imageView;
        this.frameRess = getFrameRess(frameRes_head, totle);
        this.frameCount = totle - 1;
        initLruCache();
    }

    public void startOnce() {
        preDisplay(frameRess);
        while (true) {
            if (checkCached(frameRess)) {
                displayImage(frameRess[0]);
                playConstantOnce(1);
                break;
            }
        }
    }

    /**
     * 帧动画只进行一次
     *
     * @param parentView 如果不需要父控件消失, 此参数可以为 null .
     */
    public void startOnceAndGone(@Nullable View parentView) {
        preDisplay(frameRess);
        if (parentView != null) {
            this.parentView = parentView;
            this.parentView.bringToFront();
            this.parentView.setVisibility(View.VISIBLE);
        } else {
            this.parentView = null;
        }
        imageView.bringToFront();
        imageView.setVisibility(View.VISIBLE);
        while (true) {
            if (checkCached(frameRess)) {
                displayImage(frameRess[0]);
                playConstantOnceAndGone(1);
                break;
            }
        }
    }

    public void startLoop() {
        if (!loop) {
            this.loop = true;
            preDisplay(frameRess);
            while (true) {
                if (checkCached(frameRess)) {
                    displayImage(frameRess[0]);
                    playConstantLoop(1);
                    break;
                }
            }
        }
    }

    public void stopLoop() {
        this.loop = false;
    }

    private void initLruCache() {
        lruCacheManager = new LruCacheManager<>();
    }

    private int[] getFrameRess(@DrawableRes int frameRes_head, int totle) {
        long head = frameRes_head;
        int[] frameRess = new int[totle];
        for (int i = 0; i < totle; i++, head++) {
            frameRess[i] = (int) (head);
        }
        return frameRess;
    }

    private void playConstantOnce(final int frameNo) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayImage(frameRess[frameNo]);
                if (frameNo == frameCount) {
                    lruCacheManager.clearLruCache();
                } else {
                    playConstantOnce(frameNo + 1);
                }
            }
        }, duration);
    }

    private void playConstantOnceAndGone(final int frameNo) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayImage(frameRess[frameNo]);
                if (frameNo == frameCount) {
                    if (parentView != null) {
                        parentView.setVisibility(View.GONE);
                        parentView = null;
                    }
                    imageView.setVisibility(View.GONE);
                    lruCacheManager.clearLruCache();
                } else {
                    playConstantOnceAndGone(frameNo + 1);
                }
            }
        }, duration);
    }

    private void playConstantLoop(final int frameNo) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loop) {
                    displayImage(frameRess[frameNo]);
                    if (frameNo == frameCount) {
                        playConstantLoop(0);
                    } else {
                        playConstantLoop(frameNo + 1);
                    }
                } else {
                    lruCacheManager.clearLruCache();
                }
            }
        }, duration);
    }

    private void preDisplay(int[] frameRess) {
        for (int ResId : frameRess) {
            if (lruCacheManager.get(ResId) == null || lruCacheManager.get(ResId).getBitmap().isRecycled()) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = false;
                opt.inPreferredConfig = Bitmap.Config.ALPHA_8;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    opt.inPurgeable = true;
                    opt.inInputShareable = true;
                }
                InputStream inputStream = context.getResources().openRawResource(ResId);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                lruCacheManager.put(ResId, bitmapDrawable);
            }
        }
    }

    private boolean checkCached(int[] frameRess) {
        boolean checkResult = true;
        for (int ResId : frameRess) {
            if (lruCacheManager.get(ResId) == null) {
                checkResult = false;
            }
        }
        return checkResult;
    }

    private void displayImage(@RawRes int ResId) {
        if (lruCacheManager.get(ResId) == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(lruCacheManager.get(ResId));
        } else {
            imageView.setBackgroundDrawable(lruCacheManager.get(ResId));
        }
    }
}
