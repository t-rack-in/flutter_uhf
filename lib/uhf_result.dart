import 'package:flutter/services.dart';

class UhfResult{
  final bool isSuccess;
  final String message;
  final String errorCode;
  final dynamic detail;
  UhfResult(this.isSuccess,this.message,{this.detail,this.errorCode});
  static createFail(PlatformException e)=>UhfResult(false,e.message,detail: e.details,errorCode:e.code);
  static createSuccess(String message)=>UhfResult(true,message);
}