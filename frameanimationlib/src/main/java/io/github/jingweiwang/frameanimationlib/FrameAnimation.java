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
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FrameAnimation {
    private final String TAG = getClass().getSimpleName();
    private int duration = 50;
    private Context context;
    private ImageView imageView;
    private Handler handler = new Handler();
    private LruCacheManager<Integer, BitmapDrawable> lruCacheManager;
    private Bitmap.Config bitmapConfig = Bitmap.Config.ALPHA_8;
    private int[] frameRess;
    private int frameCount;
    private boolean oneShot = false;
    private boolean running = false;
    private int curFrame = 0;
    private List<FrameAnimationCallBack> cbFrameAnimationList = new ArrayList<>();

    /**
     * 初始化一个帧序列动画资源
     *
     * @param context   当前上下文
     * @param imageView 容器
     * @param frameRess 图像资源号数组
     */
    public FrameAnimation(Context context, ImageView imageView, int[] frameRess) {
        this.context = context.getApplicationContext();
        this.imageView = imageView;
        this.frameRess = frameRess;
        this.frameCount = frameRess.length - 1;
        initLruCache();
    }

    /**
     * 初始化一个帧序列动画资源
     *
     * @param context       当前上下文
     * @param imageView     容器
     * @param frameRes_head 首个图像资源号
     * @param totle         图像资源个数
     */
    @Deprecated
    public FrameAnimation(Context context, ImageView imageView, @DrawableRes int frameRes_head, int totle) {
        this.context = context.getApplicationContext();
        this.imageView = imageView;
        this.frameRess = getFrameRess(frameRes_head, totle);
        this.frameCount = totle - 1;
        initLruCache();
    }

    /**
     * Starts the animation from the first frame, looping if necessary.
     */
    public void start() {
        if (running) {
            Log.w(TAG, "This frame animation is already running.");
            return;
        }
        preDisplay(frameRess);
        while (true) {
            if (checkCached(frameRess)) {
                running = true;
                setCbStart();
                playConstant(curFrame);
                break;
            }
        }
    }

    /**
     * Stops the animation at the current frame.
     */
    public void stop() {
        if (!running) {
            Log.w(TAG, "This frame animation is not running.");
            return;
        }
        running = false;
    }

    /**
     * 获取每帧的持续时间
     *
     * @return 每帧的持续时间, 单位为毫秒
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 设置每帧的持续时间
     *
     * @param duration 单位为毫秒, 默认值为50毫秒
     */
    public FrameAnimation setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    /**
     * 获取当前的重复状态
     *
     * @return 如果是 true, 则只播放一次; 否则重复播放
     */
    public boolean isOneShot() {
        return oneShot;
    }

    /**
     * Sets whether the animation should play once or repeat.
     *
     * @param oneShot Pass true if the animation should only play once
     */
    public FrameAnimation setOneShot(boolean oneShot) {
        this.oneShot = oneShot;
        return this;
    }

    /**
     * 选择图片Bitmap.Config参数
     *
     * @param config 可选 ALPHA_8, RGB_565, ARGB_8888
     */
    public FrameAnimation setBitmapConfig(Bitmap.Config config) {
        this.bitmapConfig = config;
        return this;
    }

    /**
     * 获取当前帧序数
     *
     * @return 当前帧序数
     */
    public int getCurFrame() {
        return curFrame;
    }

    /**
     * 重置当前帧序数
     * <p>
     * 当使用 stop() 后调用此参数再次使用 start(), 会从首帧开始播放
     */
    public FrameAnimation reset() {
        if (running) {
            Log.w(TAG, "This frame animation is already running, forbidden to reset.");
            return this;
        }
        this.curFrame = 0;
        return this;
    }

    /**
     * Indicates whether the animation is currently running or not.
     *
     * @return true if the animation is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 清除当前动画缓存, 当不再使用时, 请手动调用此方法
     */
    public void clearCache() {
        lruCacheManager.clearLruCache();
    }

    /**
     * 设置frameAnimation回调
     */
    public FrameAnimation setFrameAnimationCallBack(FrameAnimationCallBack frameAnimationCallBack) {
        this.cbFrameAnimationList.add(frameAnimationCallBack);
        return this;
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

    private void playConstant(final int frameNo) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    if (!oneShot) {
                        displayImage(frameRess[frameNo]);
                        curFrame = frameNo;
                        if (frameNo == frameCount) {
                            playConstant(0);
                        } else {
                            playConstant(frameNo + 1);
                        }
                    } else {
                        displayImage(frameRess[frameNo]);
                        curFrame = frameNo;
                        if (frameNo == frameCount) {
                            curFrame = 0;
                            running = false;
                            setCbEnd();
                        } else {
                            playConstant(frameNo + 1);
                        }
                    }
                } else {
                    curFrame = frameNo;
                    setCbEnd();
                }
            }
        }, duration);
    }

    private void setCbStart() {
        if (cbFrameAnimationList.isEmpty()) return;
        for (FrameAnimationCallBack frameAnimationCallBack : cbFrameAnimationList) {
            if (frameAnimationCallBack != null) {
                frameAnimationCallBack.onFrameAnimationStart(this);
            }
        }
    }

    private void setCbEnd() {
        if (cbFrameAnimationList.isEmpty()) return;
        for (FrameAnimationCallBack frameAnimationCallBack : cbFrameAnimationList) {
            if (frameAnimationCallBack != null) {
                frameAnimationCallBack.onFrameAnimationEnd(this);
            }
        }
    }

    private void preDisplay(int[] frameRess) {
        for (int ResId : frameRess) {
            if (lruCacheManager.get(ResId) == null || lruCacheManager.get(ResId).getBitmap().isRecycled()) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = false;
                opt.inPreferredConfig = bitmapConfig;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    opt.inPurgeable = true;
                    opt.inInputShareable = true;
                }
                InputStream inputStream = context.getResources().openRawResource(ResId);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                lruCacheManager.put(ResId, bitmapDrawable);
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
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

    private void displayImage(@DrawableRes int ResId) {
        if (lruCacheManager.get(ResId) == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(lruCacheManager.get(ResId));
        } else {
            imageView.setBackgroundDrawable(lruCacheManager.get(ResId));
        }
    }

    /**
     * frameAnimation播放状态的监听回调
     */
    public interface FrameAnimationCallBack {
        /**
         * 当frameAnimation开始时回调此方法
         *
         * @param frameAnimation 当前frameAnimation对象
         */
        void onFrameAnimationStart(FrameAnimation frameAnimation);

        /**
         * 当frameAnimation结束时回调此方法
         *
         * @param frameAnimation 当前frameAnimation对象
         */
        void onFrameAnimationEnd(FrameAnimation frameAnimation);
    }
}