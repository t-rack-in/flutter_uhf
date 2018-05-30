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
    return await _channel.invokeMethod('openUhf');
  }
}
