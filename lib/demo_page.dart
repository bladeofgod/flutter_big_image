




import 'package:flutter/material.dart';

class DemoPage extends StatefulWidget{
  @override
  State<StatefulWidget> createState() {
    return DemoPageState();
  }

}

class DemoPageState extends State<DemoPage> {
  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    return Material(
      child: Container(
        color: Colors.white,
        width: size.width,height: size.height,
        child: InteractiveViewer(
          onInteractionUpdate: (update){
            debugPrint('scale : ${update.scale}');
          },
          child: Image.asset('assets/images/1.jpg'),
        ),
      ),
    );
  }
}