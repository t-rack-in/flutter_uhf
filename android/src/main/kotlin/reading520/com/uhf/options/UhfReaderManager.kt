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
    private var listener: OnScanListener? = null
    var password: String = ""
    var password2: String = ""
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

    interface OnScanListener {
        fun onRecive(epc: String)
    }

    fun setOnScanListener(listener: OnScanListener) {
        this.listener = listener
    }

    /**
     * 开始扫描
     */
    fun startScanning(): Boolean {
        if (uhfReader == null || uhfReaderDevice == null) {
            return false
        }
        if (startFlag) {
            try {
                val localFileWriterOn = FileWriter(File(FILE_WRITE))
                localFileWriterOn.write("1")
                localFileWriterOn.close()
            } catch (e: Exception) {
                println("异常")
            }
        }
        if (!scanThread.isAlive && !startFlag) {
            scanThread.start()
            startFlag = true
        }

        isScan = true

        return true
    }

    internal inner class InventoryThread : Thread() {
        override fun run() {
            super.run()
            while (startFlag) {
                //	reader.stopInventoryMulti()
                if (!isScan) {
                    continue
                }
                uhfReader?.run {
                    inventoryRealTime()?.run {
                        map {
                            Tools.Bytes2HexString(it, it.size)
                        }.forEach {
                            listener?.run {
                                onRecive(it)
                            }
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
        var path = getSerialPortPath(context)
        UhfReader.setPortPath(path)
        uhfReader = UhfReader.getInstance()

        try {
            return uhfReader?.run {
                setOutputPower(26)
                val localFileWriterOn = FileWriter(File(FILE_WRITE))
                localFileWriterOn.write("1")
                localFileWriterOn.close()
                if (firmware != null) {
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

    /**
     * 修改电子标签
     */
    fun changeEpcContent(epc: String, newEpc: String): UhfResult {
        var result: UhfResult = UhfResult.Fail(ErrorCode.CHANGE_LABEL_FAIL)
        uhfReader?.let {
            try {
                println("$epc-$newEpc")
                it.selectEpc(Tools.HexString2Bytes(epc))
                val hexEpc = Tools.HexString2Bytes(newEpc)
                var writeFlag = it.writeTo6C(Tools.HexString2Bytes(password), 1, 2, hexEpc.size / 2, hexEpc)
                if (!writeFlag) {
                    writeFlag = it.writeTo6C(Tools.HexString2Bytes(password2), 1, 2, hexEpc.size / 2, hexEpc)
                }
                if (writeFlag) {
                    result = UhfResult.Success("电子标签修改成功")
                }
            } catch (e: Exception) {
                println(e)
                result = UhfResult.Fail(ErrorCode.CHANGE_LABEL_FAIL)
            }
        }
        if (!isConnect() || !isPowerOpen()) {
            result = UhfResult.Fail(ErrorCode.POWER_OR_SERIAL_NOT_OPEN)
        }
        return result

    }

    //修改电子标签长度
    fun changeLabelLength(epc: String, length: String): UhfResult {
        var result: UhfResult = UhfResult.Fail(ErrorCode.CHANGE_LABEL_FAIL)
        if (isConnect() && isPowerOpen()) {
            try {
                uhfReader?.let {
                    println("$epc-$length")
                    it.selectEpc(Tools.HexString2Bytes(epc))
                    val dataArray = Tools.HexString2Bytes(length)
                    val writeFlag = it.writeTo6C(Tools.HexString2Bytes(password), 1, 1, dataArray.size / 2, dataArray)
                    if (!writeFlag) {
                        val lockFlag = it.lock6C(Tools.HexString2Bytes(password2), 2, 0)
                        if (lockFlag && it.writeTo6C(Tools.HexString2Bytes(password2), 1, 1, dataArray.size / 2, dataArray)) {
                            result = UhfResult.Success("change success")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            result = UhfResult.Fail(ErrorCode.POWER_OR_SERIAL_NOT_OPEN)
        }
        return result
    }

    //停止扫描
    fun stopScanning() {
        isScan = false

    }

    fun closeSave() {
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
        closeSave()
        uhfReaderDevice?.psampoweroff()
    }

    //关闭读写器
    fun close() {
        psampoweroff()
        try {
            scanThread.interrupt()
        } catch (e: Throwable) {
        }
        uhfReader?.close()
        uhfReader = null
    }
}

enum class ErrorCode private constructor(val text: String) {
    SERIAL_PORT_INIT_ERROR("串口初始化失败"),
    SERIAL_PORT_NOT_INIT("串口未初始化"),
    OPEN_POWER_ERROR("电源开启失败"),
    POWER_NOT_OPEN("电源未开启"),
    POWER_OR_SERIAL_NOT_OPEN("串口或电源为开启初始化"),
    CHANGE_LABEL_FAIL("电子标签修改失败"),
    EPC_FORMAT_ERR("电子标签必须是 0-9a-f"),
}

sealed class UhfResult {
    class Success(val text: String) : UhfResult()
    class Fail(var code: ErrorCode) : UhfResult()
}