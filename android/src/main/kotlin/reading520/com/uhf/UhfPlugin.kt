package reading520.com.uhf
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import reading520.com.flutteruhf.options.UhfReaderDevice
class UhfPlugin: MethodCallHandler {
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "uhf")
      channel.setMethodCallHandler(UhfPlugin())
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when {
      call.method == "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      call.method == "openUhf" -> {
        UhfReaderDevice.getInstance()?.let {
          result.success("打开电源成功")
        }?:result.success("打开电源失败")
      }
      else -> result.notImplemented()
    }
  }
}
