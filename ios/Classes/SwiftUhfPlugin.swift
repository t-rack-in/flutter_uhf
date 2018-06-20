import Flutter
import UIKit
    
public class SwiftUhfPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "uhf", binaryMessenger: registrar.messenger())
    let instance = SwiftUhfPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if call.method=="getPlatformVersion" {
          result("iOS " + UIDevice.current.systemVersion)
    }else if(call.method == "isSupport"){
        result(false)
    }else{
        result(FlutterMethodNotImplemented)
    }
  
  }
}
