import 'dart:async';

import 'package:flutter/services.dart';
import 'package:uhf/uhf_result.dart';
class Uhf {

  static const MethodChannel _channel =
      const MethodChannel('uhf');
   static Future<UhfResult> invoke(String method) async{
     UhfResult result;
     try {
       final text=await _channel.invokeMethod(method);
       result= UhfResult.createSuccess(text);
     } on PlatformException catch (e) {
       result= UhfResult.createFail(e);
     }
     return result;
   }


  /// 获取平台号
  static Future<String> get platformVersion async {
    final result = await invoke("getPlatformVersion");
    return result.message;
  }
  //开启电源
  static Future<UhfResult> get openUhf async {
    return await invoke('openUhf');
  }
  //开启电源
  static Future<UhfResult> get connectAndOpenUhf async {
    return await invoke('connectAndOpenUhf');
  }
  //初始化串口
  static Future<UhfResult> get connect async {
    return await invoke("connect");
  }
  //开始扫描
  static Future<bool> get startScan async {
    return await _channel.invokeMethod("startScan");
  }
  //关闭电源
  static Future<void> get closeUhf async {
    return await _channel.invokeMethod('closeUhf');
  }
  //是否支持
  static Future<bool> get supportUhf async {
    return await _channel.invokeMethod('isSupport');
  }
  //是否支持
  static Future<bool> get isPowerOpen async {
    return await _channel.invokeMethod('isPowerOpen');
  }
}
