package reading520.com.uhf.options

import android.content.Context
import com.magicrf.uhfreaderlib.reader.Tools
import com.magicrf.uhfreaderlib.reader.UhfReader
import reading520.com.flutteruhf.options.UhfReaderDevice
import java.io.File
import java.io.FileWriter

/**
 * Created by wangyang on 2018/5/30.
 * qq:1440214507
 * des:uhf管理
 */
class UhfReaderManager private constructor() {
    private var uhfReaderDevice: UhfReaderDevice? = null
    private var uhfReader: UhfReader? = null
    private var startFlag = false
    private var isScan = false
    private val scanThread: InventoryThread = InventoryThread()

    //是否连接模块
    fun isConnect() = uhfReader != null

    //是否开启电源
    fun isPowerOpen() = uhfReaderDevice != null

    companion object {
        private val uhfReaderManager: UhfReaderManager = UhfReaderManager()
        fun getInstance() = uhfReaderManager
        const val PORT_PATH: String = "portPath"
        const val SERIAL_PORT_PATH: String = "/dev/ttyS2"
        const val SHARE_PREFERENCES: String = "share_preferences"
        const val FILE_WRITE: String = "/proc/gpiocontrol/set_id"
    }

    private fun getSerialPortPath(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(PORT_PATH, SERIAL_PORT_PATH)
    }

    /**
     * 开始扫描
     */
    fun startScanning():Boolean {
        if (uhfReader==null || uhfReaderDevice==null){
            return false
        }
        startFlag = true
        isScan = true
        if (!scanThread.isAlive) {
            scanThread.start()
        }
        return true
    }

    internal inner class InventoryThread : Thread() {
        override fun run() {
            super.run()
            while (startFlag) {
                //	reader.stopInventoryMulti()
                if (!isScan) {
                    return
                }
                uhfReader?.run {
                    inventoryRealTime()?.run {
                        map {
                            Tools.Bytes2HexString(it, it.size)
                        }.forEach {
                            print(it)
                        }
                        try {
                            Thread.sleep(40)
                        } catch (e: InterruptedException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    /**
     * 测试连接模块
     * 串口初始化
     */
    fun connect(context: Context): Boolean {
        if (uhfReader != null) return true
        UhfReader.setPortPath(getSerialPortPath(context))
        uhfReader = UhfReader.getInstance()
        try {
            return uhfReader?.run {
                setOutputPower(26)
                val localFileWriterOn = FileWriter(File(FILE_WRITE))
                localFileWriterOn.write("1")
                localFileWriterOn.close()
                if (firmware!= null) {
    //				reader.setWorkArea(3);//设置成欧标
                }
                true
            } ?: false
        } catch (e: Exception) {
            println(e)
            return false
        }
    }

    /**
     * 开启电源
     */
    fun psampoweron(): UhfResult {
        uhfReader ?: return UhfResult.Fail(ErrorCode.SERIAL_PORT_NOT_INIT)
        return uhfReaderDevice?.let {
            UhfResult.Success("power is opened")
        } ?: let {
            UhfReaderDevice.getInstance()?.let {
                uhfReaderDevice = it
                UhfResult.Success("power open success")
            } ?: UhfResult.Fail(ErrorCode.OPEN_POWER_ERROR)
        }
    }

    //停止扫描
    fun stopScanning() {
        isScan = false
        //关闭
        try {
            val localFileWriterOff = FileWriter(File(FILE_WRITE))
            localFileWriterOff.write("0")
            localFileWriterOff.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭电源
     */
    fun psampoweroff() {
        stopScanning()
        uhfReaderDevice?.psampoweroff()
    }

    //关闭读写器
    fun close() {
        uhfReaderDevice?.psampoweroff()
        uhfReader?.close()
        uhfReader = null
    }
}

enum class ErrorCode private constructor(val text: String) {
    SERIAL_PORT_INIT_ERROR("serialport init fail"),//串口为初始化
    SERIAL_PORT_NOT_INIT("serialport not init"),//串口为初始化
    OPEN_POWER_ERROR("power open fail")//电源开启失败
}

sealed class UhfResult {
    class Success(val text: String) : UhfResult()
    class Fail(var code: ErrorCode) : UhfResult()
}