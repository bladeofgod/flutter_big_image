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
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
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


    private ImageEventChannel imageEventChannel;

    private MethodChannel methodChannel;
    private WeakReference<Activity> mActivity;
    private InputStream is;

    public ImageDecoderPlugin(Activity mActivity) {
        this.mActivity = new WeakReference<>(mActivity);
        is = mActivity.getResources().openRawResource(R.raw.big5m);

        initDecoder();
    }

    private void logger(String info){
        Log.d("android " , info);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method){
            case ORDER_DECODE:
                if(ratio == 0.0){
                    ratio = (double) imageH/DensityUtil.dip2px(mActivity.get(),call.argument("viewH"));
                }

                onSizeChanged(call);

                final byte[] datas = decodeBitmap();
                if(datas == null) return;
                logger(datas.toString());
                imageEventChannel.sinkData(datas);

                break;
            default:
                break;
        }

    }

    private void refineRect(){

        if(rect.left < 0 || rect.top < 0
                ||rect.right > imageW || rect.bottom > imageH){
            logger("outer");
            rect.set(originRect);
        }else{

        }
        logger("refine " + rect.toString());
    }

    private byte[] decodeBitmap(){
        //options.inBitmap = bitmap;
        //refineRect();
        bitmap = regionDecoder.decodeRegion(rect,options);
        if(bitmap == null) return  null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        return baos.toByteArray();
    }


    private final Rect rect = new Rect();
    private double scale,currentScale;
    private double viewW,viewH;

    private final int testW = 400,testH = 400;

    private void onSizeChanged(MethodCall call){
        viewW = DensityUtil.dip2px(mActivity.get(),call.argument("viewW"));
        viewH = DensityUtil.dip2px(mActivity.get(),call.argument("viewH"));
//        scale = 1;
//        logger("" + viewH + viewW +  scale);
//        rect.left =Math.abs((int) ((double)call.argument("left")/scale));
//        rect.top =Math.abs((int) ((double)call.argument("top")/scale));
//        rect.right = Math.abs((int)((double)call.argument("width")/scale));
//        rect.bottom = Math.abs((int) ((double)call.argument("height")/scale));
//        final int right =(int) ((rect.right + rect.left)/scale);
//        final int bottom = (int) ((rect.bottom  + rect.top)/scale);
//        rect.right = Math.min(right,imageW)  ;
//        rect.bottom = Math.min(bottom,imageH);
        rect.left -= (int)((double) call.argument("left"));
        rect.top -= (int)((double)call.argument("top"));
        rect.right = rect.left + testW;
        rect.bottom = rect.top + testH;
        logger("ratio " + ratio);
        logger("rect : " + rect.toString());
        //scale = rect.right / imageW;
        //currentScale = scale;

        adjustRect();

    }
    private void adjustRect(){
        rect.top = Math.max(rect.top, 0);
        rect.left = Math.max(rect.left, 0);
        rect.top = Math.min(rect.top, imageH-testH);
        rect.left = Math.min(rect.left, imageW-testW);
        rect.right = Math.min(rect.right, imageW);
        rect.bottom = Math.min(rect.bottom, imageH);
        rect.right = Math.max(rect.right, testW);
        rect.bottom = Math.max(rect.bottom, testH);
        logger("adjust : " + rect.toString());
    }


    private BitmapFactory.Options options;

    private BitmapRegionDecoder regionDecoder;

    private Bitmap bitmap;

    private final Rect originRect = new Rect();

    //原图尺寸
    private int imageW,imageH;

    ///view 和 图片的 比
    private double ratio = 0.0;

    private void initDecoder(){

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,null,options);
        imageW = options.outWidth;
        imageH = options.outHeight;

        //临时写一下
//        logger(" image h " + imageH);
//        logger("dip 2 px  " + DensityUtil.dip2px(mActivity.get(),1080));


        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        originRect.left = 0;
        originRect.top = 0;
        originRect.right = imageW;
        originRect.bottom = imageH;

        logger("origin rect " + originRect.toString());
        try {
            regionDecoder = BitmapRegionDecoder.newInstance(is,false);
            //bitmap = regionDecoder.decodeRegion(originRect,options);
            //logger("  bitmap  " + bitmap.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        imageEventChannel = ImageEventChannel.getSingleton(binding);
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
