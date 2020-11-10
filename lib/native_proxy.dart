/*
* Author : LiJiqqi
* Date : 2020/11/9
*/

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

_NativeProxy nativeProxy = new _NativeProxy();


class _NativeProxy{
  static const String EVENT_CHANNEL = "lijiaqi.event";
  static const String PLUGIN_NAME = "com.lijiaqi.flutter_big_image";
  static const String ORDER_DECODE = 'order_decode';

  final EventChannel eventChannel =  EventChannel(EVENT_CHANNEL);
  final MethodChannel methodChannel =  MethodChannel(PLUGIN_NAME);
  
  void onSizeChange({Map args})async{
    debugPrint('invoke');
    return await methodChannel.invokeMethod(ORDER_DECODE,args);
  }



}

















