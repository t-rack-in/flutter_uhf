import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:uhf/uhf.dart';
import 'package:uhf/uhf_result.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  GlobalKey<ScaffoldState> _scaffoldStateKey = new GlobalKey<ScaffoldState>();
  TextEditingController controller =new TextEditingController(text: "");
  TextEditingController controller2 =new TextEditingController(text: "");
  List<String> epcs = new List();

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    close();
    super.dispose();
  }

  Future<void> close() async {
    await Uhf.destroyed;
  }

  Future<void> initPlatformState() async {
    String platformVersion = await Uhf.platformVersion;
    showResult(platformVersion);
  }

  Future<void> connectAndOpenUhf() async {
    UhfResult result = await Uhf.connectAndOpenUhf;
    showResult(result.message);
  }

  Future<void> startScan() async {
    bool success = await Uhf.startScan;
    showResult(success ? "开启成功" : "开启失败");
  }
  void stopScan()  {
    Uhf.stopScan;
    showResult("已关闭");
  }
  Future<void> isSupport() async {
    final isSupport = await Uhf.supportUhf;
    showResult(isSupport ? "support" : "not support");
    EventChannel eventChannel = new EventChannel("flutter.io/uhf/uhf");
    eventChannel.receiveBroadcastStream().listen((dynamic data) {
      if (!epcs.contains(data.toString())) {
        setState(() {
          epcs.add(data.toString());
        });
      }
    }, onError: (Object obj) {
      final PlatformException e = obj;
      showResult("扫描发生错误" + e.details);
    });
  }

  void showResult(String openMessage) {
    _scaffoldStateKey.currentState
        .showSnackBar(new SnackBar(content: new Text(openMessage)));
  }
  void changeLabel(String epc) async{
     String message =await Uhf.changeLabelLength(epc, controller2.value.text);
     showResult(message);
  }
  void changeNewLabel(String epc) async{
    String message =await Uhf.changeLabelContent(epc, controller.value.text);
    showResult(message);
  }
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
        home: new Scaffold(
            key: _scaffoldStateKey,
            appBar: new AppBar(
              title: const Text('Plugin example app'),
            ),
            body: new Column(
              children: <Widget>[
                new GridView.count(
                  shrinkWrap: true,
                  crossAxisSpacing: 12.0,
                  mainAxisSpacing: 3.0,
                  children: <Widget>[
                    new RaisedButton(
                        onPressed: initPlatformState, child: new Text("系统检查")),
                    new RaisedButton(
                        onPressed: isSupport, child: new Text("is support?")),
                    new RaisedButton(
                      onPressed: connectAndOpenUhf,
                      child: new Text("连接并开启电源"),
                    ),
                    new RaisedButton(
                      onPressed: startScan,
                      child: new Text("开始盘存"),
                    ),
                    new RaisedButton(
                      onPressed: stopScan,
                      child: new Text("关闭存盘"),
                    ),
                    new RaisedButton(
                      onPressed: close,
                      child: new Text("关闭"),
                    ),
                  ],
                  crossAxisCount: 4,
                ),
                new TextField(controller: controller,decoration: new InputDecoration(hintText: "请输入需要修改的电子标签号0-9a-f"),maxLength: 24,),
                new TextField(controller: controller2,decoration: new InputDecoration(hintText: "请输入需要修改的长度"),maxLength: 4,),
                new ListView.builder(
                  shrinkWrap: true,
                  itemBuilder: (BuildContext ctx, int index) {
                    return new Row(
                      children: <Widget>[
                        new Text(epcs[index]),
                        new RaisedButton(
                          onPressed: (){
                            changeLabel(epcs[index]);
                          },
                          child: new Text("修改长度"),
                        ),
                        new RaisedButton(
                          onPressed: (){
                            changeNewLabel(epcs[index]);
                          },
                          child: new Text("修改标签"),
                        ),

                      ],
                    );
                  },
                  itemCount: epcs.length,
                )
              ],
            )));
  }
}
