import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:uhf/uhf.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  GlobalKey<ScaffoldState> _scaffoldStateKey = new GlobalKey<ScaffoldState>();
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Uhf.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future<void> connectUhf() async {
    final openMessage =  await Uhf.openUhf;
    showResult(openMessage);
  }

  void showResult(String openMessage) {
     _scaffoldStateKey.currentState.showSnackBar(new SnackBar(content: new Text(openMessage)));
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
          key: _scaffoldStateKey,
          appBar: new AppBar(
            title: const Text('Plugin example app'),
          ),
          body: new SingleChildScrollView(
            child: new Padding(padding:EdgeInsets.all(12.0),
            child: new Column(
              children: <Widget>[
                new RaisedButton(onPressed:connectUhf, child: new Text("连接模块"),)
              ],
            ),)
          )),
    );
  }
}
