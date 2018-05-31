import 'dart:async';

import 'package:flutter/services.dart';

class Uhf {
  static const MethodChannel _channel =
      const MethodChannel('uhf');

  /// 获取平台号
  static Future<String> get platformVersion async {
    return await _channel.invokeMethod('getPlatformVersion');
  }
  //开启电源
  static Future<String> get openUhf async {
    final result = await _channel.invokeMethod('openUhf');
    return result.toString();
  }
  //初始化串口
  static Future<String> get connect async {
    return await _channel.invokeMethod('connect');
  }
  //关闭电源
  static Future<void> get closeUhf async {
    return await _channel.invokeMethod('closeUhf');
  }
  //是否支持
  static Future<bool> get supportUhf async {
    return await _channel.invokeMethod('canUse');
  }
  //是否支持
  static Future<bool> get isPowerOpen async {
    return await _channel.invokeMethod('isPowerOpen');
  }
}
