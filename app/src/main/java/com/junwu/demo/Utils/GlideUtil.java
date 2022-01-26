package com.junwu.demo.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

/**
 * 创建日期：2021/12/13
 *
 * @author Leibown
 * 文件名称： GlideUtil.java
 * 类说明：GlideUtil
 */
public class GlideUtil {

    public static final int DEFAULT_HEIGHT = 300;
    public static final int DEFAULT_WEIGHT = 300;

    public static final String GIF = ".gif";

    /**
     * 单例.
     */
    private static class FileManagerHolder {
        private FileManagerHolder() {
        }

        @SuppressLint("StaticFieldLeak")
        private static final GlideUtil INSTANCE = new GlideUtil();
    }

    /**
     * 初始化.
     */
    private GlideUtil() {
        super();
    }

    /**
     * 获取单例.
     */
    public static final GlideUtil getInstance() {
        return FileManagerHolder.INSTANCE;
    }

    private RequestOptions mOptions;

    /**
     * 显示图片 圆形显示  ImageView.
     *
     * @param url       图片链接
     * @param imageview 组件
     */
    public void showImageViewToCircle(ImageView imageview, String url) {
        Glide.with(imageview.getContext())
                .load(url)
                .transform(new CircleCrop())
                .into(imageview);
    }

    /**
     * 根据尺寸加载圆形图片.
     *
     * @param imageview     ImageView
     * @param url           String
     * @param defaultWeight 宽
     * @param defaultHeight 高
     */
    public void showImageViewToCircle(ImageView imageview, String url,
                                      int defaultWeight, int defaultHeight) {
        Glide.with(imageview.getContext())
                .load(url)
                .transform(new CircleCrop())
                .override(defaultWeight, defaultHeight)
                .format(DecodeFormat.PREFER_RGB_565)
                .into(imageview);
    }

    /**
     * 加载图片.
     *
     * @param imageView ImageView
     * @param path      String
     */
    public void loadPicture(final ImageView imageView, final String path) {
        // loadPicture(imageView, path, Constants.INT_0, Constants.INT_0);
        Glide.with(imageView.getContext())
                .load(path)
                .into(imageView);
    }

    /**
     * 加载图片，error.
     *
     * @param imageView ImageView
     * @param path      String
     */
    public void loadPicture(final ImageView imageView, final String path, final int defaultImage) {
        Glide.with(imageView.getContext())
                .load(path)
                .error(defaultImage)
                .into(imageView);
    }

    /**
     * 带默认图片的圆角图片.
     *
     * @param imageView    imageView
     * @param path         图片链接
     * @param defaultImage 加载时显示默认图片
     * @param radius       圆角
     */
    public void loadPictureRound(final ImageView imageView, final String path,
                                 final int defaultImage, final int radius) {
        Glide.with(imageView.getContext())
                .load(path)
                .placeholder(defaultImage)
                .transform(new RoundedCorners(radius))
                .into(imageView);
    }

    /**
     * 加载网络图片.
     *
     * @param imageView     imageView
     * @param path          图片地址
     * @param defaultWeight 宽度
     * @param defaultHeight 高度
     */
    public void loadPicture(final ImageView imageView, final String path,
                            int defaultWeight, int defaultHeight) {
        if (null == imageView || TextUtils.isEmpty(path)) {
            return;
        }
        if (mOptions == null) {
            //            cornersTransform = new RoundedCornersTransform(context, 10);
            //            cornersTransform.setNeedCorner(true, true, false, false);
            mOptions = new RequestOptions()
                    //                    .transform(cornersTransform)
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    // .error(R.drawable.skin_iflytek_filemanager_img_picture_play_error)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .fitCenter();
        }
        if (defaultHeight > 0 && defaultWeight > 0) {

            if (path.toLowerCase().endsWith(GIF)) {
                Glide.with(imageView.getContext())
                        .load(path)
                        .apply(mOptions)
                        .override(defaultWeight, defaultHeight)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imageView);
            } else {
                Glide.with(imageView.getContext())
                        .load(path)
                        .apply(mOptions)
                        .override(defaultWeight, defaultHeight)
                        .into(imageView);
            }
        } else {
            Glide.with(imageView.getContext())
                    .load(path)
                    .apply(mOptions)
                    .into(imageView);
        }
    }

    /**
     * 加载网络图片.
     *
     * @param imageView     imageView
     * @param path          图片地址
     * @param defaultWeight 宽度
     * @param defaultHeight 高度
     */
    public void loadPicture(final ImageView imageView,
                            final Fragment fragment, final String path,
                            int defaultWeight, int defaultHeight) {
        if (null == imageView || TextUtils.isEmpty(path)) {
            return;
        }
        if (mOptions == null) {
            //            cornersTransform = new RoundedCornersTransform(context, 10);
            //            cornersTransform.setNeedCorner(true, true, false, false);
            mOptions = new RequestOptions()//                    .transform(cornersTransform)
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    //.error(R.drawable.skin_iflytek_filemanager_img_picture_play_error)
                    .format(DecodeFormat.PREFER_RGB_565)
                    //加上会导致列表图标加载闪烁
                    //                    .skipMemoryCache(true)
                    .fitCenter();
        }
        if (defaultHeight > 0 && defaultWeight > 0) {
            if (path.toLowerCase().endsWith(GIF)) {
                Glide.with(fragment)
                        .asGif()
                        .load(path)
                        .apply(mOptions)
                        .override(defaultWeight, defaultHeight)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imageView);
            } else {
                Glide.with(fragment)
                        .asDrawable()
                        .load(path)
                        .apply(mOptions)
                        .override(defaultWeight, defaultHeight)
                        .into(imageView);
            }
        } else {
            Glide.with(fragment)
                    .load(path)
                    .apply(mOptions)
                    .into(imageView);
        }
    }

    public void loadPictureForList(
            final ImageView imageView, final Fragment fragment, final String path) {
        loadPicture(imageView, fragment, path, DEFAULT_WEIGHT, DEFAULT_HEIGHT);
    }

    /**
     * 清楚图片加载缓存.
     *
     * @param activity 界面
     */
    public void clearImageMemoryCache(Activity activity) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(activity).clearMemory();
                activity = null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 回收图片控件.
     */
    public void recycle(ImageView view) {
        Drawable drawable = view.getDrawable();
        if (drawable instanceof GifDrawable) {
            GifDrawable gifDrawable = (GifDrawable) drawable;
            gifDrawable.stop();
            gifDrawable.clearAnimationCallbacks();
            gifDrawable = null;
        }
        Bitmap bitmap = view.getDrawingCache();
        if (null != bitmap && !bitmap.isRecycled()) {
            view.setImageBitmap(null);
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 回收图片控件.
     */
    public void recycleForGlide(ImageView view) {
        if (view == null) {
            return;
        }
        Glide.with(view.getContext()).clear(view);
    }

    /**
     * 回收图片控件.
     */
    public void recycleForGlide(Fragment fragment, ImageView view) {
        if (view == null || fragment == null) {
            return;
        }
        Glide.with(fragment).clear(view);
    }
}