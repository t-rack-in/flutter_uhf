package reading520.com.uhf

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import reading520.com.uhf.options.ErrorCode
import reading520.com.uhf.options.UhfReaderManager
import reading520.com.uhf.options.UhfResult
import java.nio.ByteBuffer

class UhfPlugin private constructor(private val registrar: Registrar) : MethodCallHandler {
    private val uhfReaderManager = UhfReaderManager.getInstance()
    private val sharedPreferences: SharedPreferences by lazy { registrar.context().getSharedPreferences(UhfReaderManager.SHARE_PREFERENCES, Context.MODE_PRIVATE) }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "uhf")
            channel.setMethodCallHandler(UhfPlugin(registrar))

        }

    }

    /**
     * 是否使用uhf
     */
    @SuppressLint("PrivateApi")
    private fun uhfCanUse(): Boolean {
        var canUse = false
        //是否坚持过本机可用uhf读写功能
        if (!sharedPreferences.getBoolean("IS_TEST", false)) {
            val edit = sharedPreferences.edit()
            try {
                Class.forName("android.os.IScanService\$Stub")
                edit.putBoolean("CAN_SCAN", true)
                canUse = true
            } catch (e: Exception) {
                edit.putBoolean("CAN_SCAN", false)
            }
            edit.putBoolean("IS_TEST", true).apply()
        } else {
            canUse = sharedPreferences.getBoolean("CAN_SCAN", false)
        }
        return canUse
    }

    override fun onMethodCall(call: MethodCall, result: Result) {

        when {
            call.method == "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
                registrar.messenger().send("getData", ByteBuffer.wrap("i am boy".toByteArray()))
            }
            call.method == "isSupport" -> result.success(uhfCanUse())
            call.method == "isPowerOpen" -> result.success(uhfReaderManager.isPowerOpen())
            call.method=="openUhf"->{
                val powerResult = UhfReaderManager.getInstance().psampoweron()
                when (powerResult) {
                    is UhfResult.Success -> result.success(powerResult.text)
                    is UhfResult.Fail -> result.error("-1", powerResult.code.text, null)
                }
            }
            call.method == "connectAndOpenUhf" -> {
                if (!uhfReaderManager.isConnect() && !uhfReaderManager.connect(registrar.context())) {
                    result.error("-1", ErrorCode.SERIAL_PORT_INIT_ERROR.text, null)
                }
                val powerResult = UhfReaderManager.getInstance().psampoweron()
                when (powerResult) {
                    is UhfResult.Success -> result.success(powerResult.text)
                    is UhfResult.Fail -> result.error("-1", powerResult.code.text, null)
                }

            }
            call.method == "closeUhf" -> {
                uhfReaderManager.psampoweroff()
            }
            call.method == "stopScan" -> {
                uhfReaderManager.stopScanning()
            }
            call.method == "startScan" -> {
                result.success(uhfReaderManager.startScanning())
            }
            call.method == "close" -> {
                uhfReaderManager.close()
            }
            call.method == "connect" -> {
                if (uhfReaderManager.connect(registrar.context())) {
                    result.success("connect success")
                } else {
                    result.error("-1", "connect fail", null)
                }
            }

            else -> result.notImplemented()
        }
    }
}
