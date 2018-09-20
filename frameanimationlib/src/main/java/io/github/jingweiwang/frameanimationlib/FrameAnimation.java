/*
 *    Copyright 2018 JingweiWang
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
import android.support.annotation.IntRange;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FrameAnimation {
    private final String TAG = "FrameAnimation";

    private final Context context;
    private final ImageView imageView;
    private final Handler handler = new Handler();
    private final List<FrameAnimationCallBack> cbFrameAnimationList = new ArrayList<>();

    private LruCacheManager<Integer, BitmapDrawable> lruCacheManager;
    private Bitmap.Config bitmapConfig = Bitmap.Config.RGB_565;
    private InputStream inputStreamForDisplayImage;
    private Bitmap bitmapForDisplayImage;
    private BitmapDrawable bitmapDrawableForDisplayImage;
    private BitmapFactory.Options optionsForDisplayImage;

    private int[] frameRess;
    private int frameRessBound;
    private boolean oneShot = false;
    private boolean running = false;
    private int duration = 50;
    private int curFrame = 0;
    private boolean openLog = false;

    private FrameAnimation(Context context, ImageView imageView) {
        this.context = context.getApplicationContext();
        this.imageView = imageView;
    }

    /**
     * 初始化一个帧序列动画资源
     * <p>
     * 默认LruCache的总容量的大小为当前进程可用内存的(1/8)
     *
     * @param context   当前上下文
     * @param imageView 容器
     * @param frameRess 图像资源号数组
     */
    public FrameAnimation(Context context, ImageView imageView, int[] frameRess) {
        this(context, imageView);
        this.frameRess = frameRess;
        this.frameRessBound = frameRess.length - 1;
        initLruCache();
    }

    /**
     * 初始化一个帧序列动画资源
     *
     * @param context          当前上下文
     * @param distributionRate LruCache的总容量的大小为当前进程可用内存的(1/distributionRate), 只可从4和8中选取, 默认值为8
     * @param imageView        容器
     * @param frameRess        图像资源号数组
     */
    public FrameAnimation(Context context, int distributionRate, ImageView imageView, int[] frameRess) {
        this(context, imageView);
        this.frameRess = frameRess;
        this.frameRessBound = frameRess.length - 1;
        initLruCache(distributionRate);
    }

    /**
     * 初始化一个帧序列动画资源
     * <p>
     * 默认LruCache的总容量的大小为当前进程可用内存的(1/8)
     *
     * @param context     当前上下文
     * @param imageView   容器
     * @param prefix      图像资源名前缀
     * @param firstSuffix 首个图像资源名的后缀
     * @param numberBit   图像资源名后缀位数, 如果为0则按照实际位数设置
     * @param total       图像资源个数
     */
    public FrameAnimation(Context context, ImageView imageView, String prefix,
                          @IntRange(from = 0) int firstSuffix, @IntRange(from = 0, to = 5) int numberBit,
                          @IntRange(from = 1) int total) {
        this(context, imageView);
        this.frameRess = getFrameRess(prefix, firstSuffix, numberBit, total);
        this.frameRessBound = frameRess.length - 1;
        initLruCache();
    }

    /**
     * 初始化一个帧序列动画资源
     *
     * @param context          当前上下文
     * @param distributionRate LruCache的总容量的大小为当前进程可用内存的(1/distributionRate), 只可从4和8中选取, 默认值为8
     * @param imageView        容器
     * @param prefix           图像资源名前缀
     * @param firstSuffix      首个图像资源名的后缀
     * @param numberBit        图像资源名后缀位数, 如果为0则按照实际位数设置
     * @param total            图像资源个数
     */
    public FrameAnimation(Context context, int distributionRate, ImageView imageView, String prefix,
                          @IntRange(from = 0) int firstSuffix, @IntRange(from = 0, to = 5) int numberBit,
                          @IntRange(from = 1) int total) {
        this(context, imageView);
        this.frameRess = getFrameRess(prefix, firstSuffix, numberBit, total);
        this.frameRessBound = frameRess.length - 1;
        initLruCache(distributionRate);
    }

    /**
     * 从第一帧开始播放帧动画, 如果设置了循环将会循环播放
     *
     * @see #setOneShot(boolean)
     * @see #isOneShot()
     */
    public void start() {
        if (running) {
            if (openLog) {
                Log.w(TAG, "This frame animation is already running.");
            }
            return;
        }

        running = true;
        setCbStart();
        playConstant(curFrame);
    }

    /**
     * 停止播放帧动画并保持显示当前帧
     */
    public void stop() {
        if (!running) {
            if (openLog) {
                Log.w(TAG, "This frame animation is not running.");
            }
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
     * @return FrameAnimation
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
     * 设置是否帧动画只播放一次
     *
     * @param oneShot 如果是 true, 则只播放一次; 否则重复播放
     * @return FrameAnimation
     */
    public FrameAnimation setOneShot(boolean oneShot) {
        this.oneShot = oneShot;
        return this;
    }

    /**
     * 选择图片Bitmap.Config参数
     *
     * @param config 可选 ALPHA_8, RGB_565, ARGB_8888等
     * @return FrameAnimation
     * @see Bitmap.Config
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
     *
     * @return FrameAnimation
     */
    public FrameAnimation reset() {
        if (running) {
            if (openLog) {
                Log.w(TAG, "This frame animation is already running, forbidden to reset.");
            }
            return this;
        }
        this.curFrame = 0;
        return this;
    }

    /**
     * 帧动画当前是否正在播放
     *
     * @return 如果是 true, 则正在播放; 否则不在播放
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
     *
     * @param frameAnimationCallBack 监听器
     * @return FrameAnimation
     */
    public FrameAnimation setFrameAnimationCallBack(FrameAnimationCallBack frameAnimationCallBack) {
        this.cbFrameAnimationList.add(frameAnimationCallBack);
        return this;
    }

    private void initLruCache() {
        initLruCache(8);
    }

    private void initLruCache(int distributionRate) {
        switch (distributionRate) {
            case 4:
                lruCacheManager = new LruCacheManager<>(4);
                break;
            case 8:
                lruCacheManager = new LruCacheManager<>(8);
                break;
            default:
                lruCacheManager = new LruCacheManager<>(8);
                Log.e(TAG, "构造器distributionRate参数只能填入4或8, 非法填入! 取默认值8!");
                break;
        }
    }

    private int[] getFrameRess(final String prefix, final int firstSuffix, final int numberBit, final int total) {
        int[] frameRess = new int[total];
        String suffix;
        for (int i = 0; i < total; i++) {
            if (0 != numberBit) {
                suffix = String.format("%0" + numberBit + "d", firstSuffix + i);
            } else {
                suffix = String.valueOf(firstSuffix + i);
            }
            frameRess[i] = context.getResources().getIdentifier(prefix + suffix,
                    "drawable", context.getPackageName());
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
                        if (curFrame == frameRessBound) {
                            playConstant(0);
                        } else {
                            playConstant(curFrame + 1);
                        }
                    } else {
                        displayImage(frameRess[frameNo]);
                        curFrame = frameNo;
                        if (curFrame == frameRessBound) {
                            curFrame = 0;
                            running = false;
                            setCbEnd();
                        } else {
                            playConstant(curFrame + 1);
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

    private int calculateInSampleSize(BitmapFactory.Options options, final int reqWidth, final int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void displayImage(final int ResId) {
        if (oneShot || lruCacheManager.get(ResId) == null) {
            if (openLog) {
                Log.w(TAG, "miss displayImage: " + lruCacheManager.printLruCache() + " ResId: " + ResId);
            }
            optionsForDisplayImage = new BitmapFactory.Options();
            optionsForDisplayImage.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), ResId, optionsForDisplayImage);
            optionsForDisplayImage.inSampleSize = calculateInSampleSize(optionsForDisplayImage,
                    imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
            optionsForDisplayImage.inJustDecodeBounds = false;
            optionsForDisplayImage.inPreferredConfig = bitmapConfig;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                optionsForDisplayImage.inPurgeable = true;
                optionsForDisplayImage.inInputShareable = true;
            }
            inputStreamForDisplayImage = context.getResources().openRawResource(ResId);
            bitmapForDisplayImage = BitmapFactory.decodeStream(inputStreamForDisplayImage,
                    null, optionsForDisplayImage);
            bitmapDrawableForDisplayImage = new BitmapDrawable(context.getResources(), bitmapForDisplayImage);
            try {
                inputStreamForDisplayImage.close();
            } catch (IOException ignored) {
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackground(bitmapDrawableForDisplayImage);
            } else {
                imageView.setBackgroundDrawable(bitmapDrawableForDisplayImage);
            }

            if (bitmapForDisplayImage.getByteCount() + lruCacheManager.getSize() < lruCacheManager.getMaxSize() && !oneShot) {
                lruCacheManager.put(ResId, bitmapDrawableForDisplayImage);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackground(lruCacheManager.get(ResId));
            } else {
                imageView.setBackgroundDrawable(lruCacheManager.get(ResId));
            }
            if (openLog) {
                Log.w(TAG, lruCacheManager.printLruCache());
            }

            recycleForDisplayImage();
        }
    }

    private void recycleForDisplayImage() {
        if (optionsForDisplayImage != null) {
            optionsForDisplayImage = null;
        }
        if (bitmapForDisplayImage != null) {
            bitmapForDisplayImage.recycle();
            bitmapForDisplayImage = null;
        }
        if (bitmapDrawableForDisplayImage != null) {
            bitmapDrawableForDisplayImage.setCallback(null);
            bitmapDrawableForDisplayImage.getBitmap().recycle();
            bitmapDrawableForDisplayImage = null;
        }
        if (inputStreamForDisplayImage != null) {
            inputStreamForDisplayImage = null;
        }
    }

    /**
     * 是否开启Log
     *
     * @return 如果为 true 则已经打开, false 为已经关闭
     */
    public boolean isOpenLog() {
        return openLog;
    }

    /**
     * Log开关
     *
     * @param openLog true 为打开, false 为关闭
     * @return FrameAnimation
     */
    public FrameAnimation setOpenLog(boolean openLog) {
        this.openLog = openLog;
        return this;
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