




import 'package:flutter/material.dart';

class DemoPage extends StatefulWidget{
  @override
  State<StatefulWidget> createState() {
    return DemoPageState();
  }

}

class DemoPageState extends State<DemoPage> {

  double currentScale = 1.0;

  final GlobalKey key = GlobalKey();

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    return Material(
      child: Container(
        color: Colors.white,
        width: size.width,height: size.height,
        child: InteractiveViewer(
          onInteractionStart: (start){

          },
          onInteractionUpdate: (update){
            log('${update.scale}');
//            currentScale *= update.scale;
//            log('current  $currentScale');

//          RenderBox renderBox = key.currentContext.findRenderObject();
//          log('${renderBox.paintBounds}');

          },
          onInteractionEnd: (end){

          },
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                key: key,
                width: 100,height: 100,
                color: Colors.blueAccent,
              )
            ],
          ),
          //child: Image.asset('assets/images/1.jpg'),
        ),
      ),
    );
  }

  void log(String info){
    debugPrint('scale $info');
  }
}