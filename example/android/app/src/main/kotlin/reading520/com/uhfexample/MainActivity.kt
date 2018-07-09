package reading520.com.uhfexample

import android.os.Bundle
import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import reading520.com.uhf.options.UhfReaderManager

class MainActivity(): FlutterActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GeneratedPluginRegistrant.registerWith(this)

  }

  override fun onDestroy() {
    UhfReaderManager.getInstance().close()
    super.onDestroy()
  }
}
