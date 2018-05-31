package reading520.com.uhf.options

import android.content.Context
import com.magicrf.uhfreaderlib.reader.UhfReader
import reading520.com.flutteruhf.options.UhfReaderDevice

/**
 * Created by wangyang on 2018/5/30.
 * qq:1440214507
 * des:
 */
class UhfReaderManager private constructor() {
    private var uhfReaderDevice: UhfReaderDevice? = null
    private var uhfReader: UhfReader? = null
    //是否连接模块
    fun isConnect()=uhfReader!=null
    //是否开启电源
    fun isPowerOpen()=uhfReaderDevice!=null
    companion object {
        private val uhfReaderManager: UhfReaderManager = UhfReaderManager()
        fun  getInstance()= uhfReaderManager
        const val PORT_PATH: String = "portPath"
        const val SERIAL_PORT_PATH: String = "/dev/ttyS2"
        const val SHARE_PREFERENCES: String = "share_preferences"
    }

    private fun getSerialPortPath(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(PORT_PATH, SERIAL_PORT_PATH)
    }

    /**
     * 测试连接模块
     * 串口初始化
     */
    fun connect(context: Context): Boolean {
        if (uhfReader!=null)return  true
        UhfReader.setPortPath(getSerialPortPath(context))
        uhfReader = UhfReader.getInstance()
        return uhfReader != null
    }

    /**
     * 开启电源
     */
    fun psampoweron(): UhfResult {
        uhfReader ?: return UhfResult.Fail(ErrorCode.CONNECT_INIT_ERROR)
        return uhfReaderDevice?.let {
            UhfResult.Success("电源已经开启过了")
        } ?: let {
            UhfReaderDevice.getInstance()?.let {
                uhfReaderDevice = it
                UhfResult.Success("电源开启成功")
            } ?: UhfResult.Fail(ErrorCode.OPEN_POWER_ERROR)
        }
    }

    /**
     * 关闭电源
     */
    fun psampoweroff() {
        uhfReaderDevice?.psampoweroff()
    }
    //关闭读写器
    fun close(){
        uhfReaderDevice?.psampoweroff()
        uhfReader?.close()
        uhfReader=null
    }
}
enum class ErrorCode private constructor(val text:String){
    CONNECT_INIT_ERROR("串口未初始化"),//串口为初始化
    OPEN_POWER_ERROR("电源开启失败")//电源开启失败
}
sealed class UhfResult {
    class Success(val text: String) : UhfResult()
    class Fail(var code: ErrorCode) : UhfResult()
}