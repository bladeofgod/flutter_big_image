package com.lijiaqi.flutter_big_image;


/*
 * Author : LiJiqqi
 * Date : 2020/11/9
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class ImageDecoderPlugin implements FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler {

    private static final String PLUGIN_NAME = "com.lijiaqi.flutter_big_image";

    private static final String ORDER_DECODE = "order_decode";


    private MethodChannel methodChannel;
    private WeakReference<Activity> mActivity;
    private InputStream is;

    public ImageDecoderPlugin(Activity mActivity) {
        this.mActivity = new WeakReference<>(mActivity);
        is = mActivity.getResources().openRawResource(R.raw.img);

        initDecoder();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method){
            case ORDER_DECODE:

                onSizeChanged(call);

                break;
            default:
                break;
        }

    }

    private Rect rect;
    private double scale,currentScale;

    private void onSizeChanged(MethodCall call){
        rect.left =(int) DensityUtil.dip2px(mActivity.get(),call.argument("left"));
        rect.top =(int) DensityUtil.dip2px(mActivity.get(),call.argument("top"));
        rect.right =(int) DensityUtil.dip2px(mActivity.get(),call.argument("width"));
        rect.bottom =(int) DensityUtil.dip2px(mActivity.get(),call.argument("height"));
        scale = rect.right / imageW;
        currentScale = scale;

    }


    private BitmapFactory.Options options;

    private BitmapRegionDecoder regionDecoder;

    //原图尺寸
    private int imageW,imageH;

    private void initDecoder(){

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,null,options);
        imageW = options.outWidth;
        imageH = options.outHeight;

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;

        try {
            regionDecoder = BitmapRegionDecoder.newInstance(is,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel = new MethodChannel(binding.getBinaryMessenger(),PLUGIN_NAME);
        methodChannel.setMethodCallHandler(this);

    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;

    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }


}
