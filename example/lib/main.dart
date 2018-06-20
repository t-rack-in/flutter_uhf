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
  @override
  void initState() {
    super.initState();
    BinaryMessages.setMessageHandler("getData", (ByteData data){
      showResult("我被调用了");
      return Future.value(data);
    });
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
    showResult(success?"开启成功":"开启失败");
  }

  Future<void> isSupport() async {
    final isSupport = await Uhf.supportUhf;
    showResult(isSupport ? "support" : "not support");
  }

  void showResult(String openMessage) {
    _scaffoldStateKey.currentState
        .showSnackBar(new SnackBar(content: new Text(openMessage)));
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
        home: new Scaffold(
      key: _scaffoldStateKey,
      appBar: new AppBar(
        title: const Text('Plugin example app'),
      ),
      body: new GridView.count(
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
          )
        ],
        crossAxisCount: 4,
      ),
    ));
  }
}
