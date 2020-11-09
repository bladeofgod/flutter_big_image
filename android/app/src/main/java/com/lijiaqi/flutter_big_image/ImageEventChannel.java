package com.lijiaqi.flutter_big_image;


/*
 * Author : LiJiqqi
 * Date : 2020/11/9
 */

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;

public class ImageEventChannel implements EventChannel.StreamHandler {

    private static final String EVENT_CHANNEL = "lijiaqi.event";

    private static volatile ImageEventChannel singleton;

    public static ImageEventChannel getSingleton(FlutterPlugin.FlutterPluginBinding binding){
        if(singleton == null){
            synchronized (ImageEventChannel.class){
                if(singleton == null){
                    singleton = new ImageEventChannel(binding);
                }
            }
        }
        return singleton;
    }
    private EventChannel.EventSink eventSink;

    public void sinkData(byte[] datas){
        if(eventSink == null){
            Log.d("event channel","data is empty");
        }else{
            eventSink.success(datas);
        }
    }

    private ImageEventChannel(FlutterPlugin.FlutterPluginBinding binding){
        EventChannel eventChannel = new EventChannel(binding.getBinaryMessenger(),EVENT_CHANNEL);
        eventChannel.setStreamHandler(this);
    }


    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.eventSink = events;

    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;

    }
}
