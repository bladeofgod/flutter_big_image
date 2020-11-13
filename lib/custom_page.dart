/*
* Author : LiJiqqi
* Date : 2020/11/9
*/


import 'dart:collection';
import 'dart:typed_data';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_big_image/native_proxy.dart';

class CustomPage extends StatefulWidget{

  final Size size;

  const CustomPage({Key key, this.size}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return CustomPageState(size);
  }

}

class CustomPageState extends State<CustomPage> {

  final Size size;
  CustomPageState(this.size);


  static const double default_width = 100;
  static const double default_height = 100;


  double viewW,viewH;

  GlobalKey _globalKey = new GlobalKey();
  //视频的参数，宽，高，比例
  double _width;
  double _height;
  double _x;
  double _y;
  double _ratio;

  //临时变量
  double _tmpW;
  double _tmpH;
  Offset _lastOffset;

  //背景的宽高
  double _bgW;
  double _bgH;

  Uint8List imageData;



  @override
  void initState() {

    _treeMap['viewW'] = size.width;
    _treeMap['viewH'] = size.height;

    nativeProxy.eventChannel.receiveBroadcastStream()
    .listen((event) {
      //success
      //debugPrint('image data $event');
      setState(() {
        imageData = event;
      });

    });

    super.initState();

    _width = size.width;
    _height = size.height;
    _x = 0;
    _y = 0;


    _ratio = _width / _height;
    _tmpW = _width;
    _tmpH = _height;
    _treeMap['left'] = _x;
    _treeMap['top'] = _y;
    _treeMap['width'] = _width;
    _treeMap['height'] = _height;

  }


  //计算背景的宽高
  void getBgInfo(){
    RenderBox renderObject = _globalKey.currentContext.findRenderObject();
    _bgW = renderObject.paintBounds.size.width;
    _bgH = renderObject.paintBounds.size.height;
  }

  //开始缩放
  void scaleStart(ScaleStartDetails details){
    _tmpW = _width;
    _tmpH = _height;
    _lastOffset = details.focalPoint;
    //getBgInfo();
  }

  final SplayTreeMap _treeMap = SplayTreeMap();



  //缩放更新
  void scaleUpdate(ScaleUpdateDetails details){
    if((_width/size.width) > 4) {
      //todo nothing
    }else{
      _width = _tmpW*details.scale;
      _height = _tmpH*details.scale;
    }
    ///只传递偏移量
    _x = (details.focalPoint.dx - _lastOffset.dx) ;
    _y = (details.focalPoint.dy - _lastOffset.dy);

    _treeMap['scale'] = details.scale;
    _treeMap['left'] = _x;
    _treeMap['top'] = _y;
    _treeMap['width'] = 400.0;
    _treeMap['height'] = 400.0;
    debugPrint(_treeMap.toString());

    //边界判定，保持宽高比
//      if(_width > _bgW){
//        _width = _bgW;
//        _height = _width / _ratio;
//      }
//      if(_height > _bgH){
//        _height = _bgH;
//        _width = _height * _ratio;
//      }
//      if(_x < 0){
//        _x = 0;
//      }
//      if(_y < 0){
//        _y = 0;
//      }
//      if(_x > _bgW-_width){
//        _x = _bgW-_width;
//      }
//      if(_y > _bgH-_height){
//        _y = _bgH-_height;
//      }
    _lastOffset = details.focalPoint;
    nativeProxy.onSizeChange(args: _treeMap);
///older code
//    setState(() {
//      _width = _tmpW*details.scale;
//      _height = _tmpH*details.scale;
//      _x += (details.focalPoint.dx - _lastOffset.dx) ;
//      _y += (details.focalPoint.dy - _lastOffset.dy);
//      //边界判定，保持宽高比
////      if(_width > _bgW){
////        _width = _bgW;
////        _height = _width / _ratio;
////      }
////      if(_height > _bgH){
////        _height = _bgH;
////        _width = _height * _ratio;
////      }
////      if(_x < 0){
////        _x = 0;
////      }
////      if(_y < 0){
////        _y = 0;
////      }
////      if(_x > _bgW-_width){
////        _x = _bgW-_width;
////      }
////      if(_y > _bgH-_height){
////        _y = _bgH-_height;
////      }
//      _lastOffset = details.focalPoint;
//    });
  }

  //缩放结束
  void scaleEnd(ScaleEndDetails details){
    _tmpW = _width;
    _tmpH = _height;
    //边界判定，保持宽高比
//    if(_width < default_width){
//      _width = default_width;
//      _height = _width / _ratio;
//    }
//    if(_height < default_height){
//      _height = default_height;
//      _width = _height * _ratio;
//    }
  }



  @override
  Widget build(BuildContext context) {

    return Container(
      width: size.width,height: size.height,
      color: Colors.white,
      child: image(),
      //child:Image.asset('assets/images/big5m.jpg'),
    );
  }

  Widget image(){
    return GestureDetector(
      onScaleUpdate: scaleUpdate,
      onScaleStart: scaleStart,
      onScaleEnd: scaleEnd,
      child: Stack(
        alignment: Alignment.center,
        children: [
          Container(
            color: Colors.grey,
            width: 400,height: 400,
            child:imageData == null ?  emptyWidget() : Image.memory(imageData,fit: BoxFit.fill,),
          )
        ],
      ),
    );
  }

  Widget emptyWidget(){
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Container(
          width: 100,height: 100,color: Colors.red,
        ),
      ],
    );
  }

  Widget stackWidget(final Size size){
    return Stack(
      children: [
        Container(
          key: _globalKey,
          color: Colors.grey,
          width: size.width,height: size.height,
        ),
        Positioned(
          left: _x,top:_y ,
          child: GestureDetector(
            onScaleUpdate: scaleUpdate,
            onScaleStart: scaleStart,
            onScaleEnd: scaleEnd,
            child: Container(
              color: Colors.blue,
              width: _width,height: _height,
            ),
          ),
        ),
      ],
    );
  }

}























