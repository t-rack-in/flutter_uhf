package com.android.hdhe.uhf.reader


import com.magicrf.uhfreaderlib.readerInterface.DevicePowerInterface

class SerialPort : DevicePowerInterface {

    override fun uhfPowerOn() {
        psampoweron()
    }

    override fun uhfPowerOff() {
        psampoweroff()
    }

    external fun psampoweron()

    external fun psampoweroff()

    companion object {

        init {
            System.loadLibrary("devapi")
            System.loadLibrary("uhf")
        }
    }
}
