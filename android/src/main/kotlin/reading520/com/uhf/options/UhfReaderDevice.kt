package reading520.com.flutteruhf.options

import com.android.hdhe.uhf.reader.SerialPort

/**
 * Created by wangyang on 2018/5/30.
 * qq:1440214507
 * des:UhfReaderDevice 管理 电源开启与关闭
 */
class UhfReaderDevice private constructor() {

    companion object {
        private var devPower: SerialPort? = null
        private  var uhfReaderDevice: UhfReaderDevice? = null
        fun getInstance(): UhfReaderDevice? {
            devPower ?: try {
                devPower = SerialPort()
            } catch (_: Exception) {
                return null
            }
            devPower?.psampoweron()
            return uhfReaderDevice ?: try {
                uhfReaderDevice = UhfReaderDevice()
                uhfReaderDevice
            } catch (_: Exception) {
                null
            }
        }
    }

    /**
     * 开启电源
     */
    fun powerOn(){
        devPower?.psampoweron()
    }

    /**
     * 关闭电源
     */
    fun powerOff(){
        devPower?.psampoweroff()
    }
}