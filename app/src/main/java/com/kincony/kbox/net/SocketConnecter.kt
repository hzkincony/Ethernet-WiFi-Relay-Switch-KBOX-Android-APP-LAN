package com.kincony.kbox.net

import android.os.Looper
import android.util.Log
import com.kincony.kbox.App
import com.kincony.kbox.R
import com.kincony.kbox.net.data.NetAddress
import java.io.IOException
import java.net.Socket
import java.util.concurrent.locks.ReentrantLock

/**
 * TCP连接类
 */
class SocketConnecter : IConnecter {
    private var zero = asciiToString("0")
    private val TAG = "SocketConnecter"
    private var isConnected = false
    private var isConnecting = false
    private var socket: Socket? = null
    @Volatile
    private var isWriting = false
    @Volatile
    private var bufferString: String? = null
    private var address: NetAddress? = null

    private var lock = ReentrantLock()
    private var callback2: ((Exception) -> Unit)?

    constructor(address: NetAddress?, callback2: ((Exception) -> Unit)?) {
        this.address = address
        this.callback2 = callback2;
    }

    override fun connect() {
        try {
            if (lock.tryLock()) {
                if (address?.isAvail() != true) throw Exception(App.application.getString(R.string.address_alert))
                isConnecting = true
                socket = Socket(address!!.ip, address!!.port!!)
                Log.i(TAG, "建立连接:(${address!!.ip}:${address!!.port!!})")
                isConnected = true
                val inputStream = socket!!.getInputStream()
                val buffer = ByteArray(1024)
                var len = -1
                do {
                    len = inputStream.read(buffer)
                    var data = String(buffer, 0, len)
                    data = data.replace(zero, "")
                    if (bufferString == null) {
                        bufferString = data
                    } else {
                        bufferString += data
                    }
                } while (len != -1)
            }
            lock.unlock()
            Log.i(TAG, "连接断开----")
        } catch (e: Exception) {
            var s = App.application.getString(R.string.connect_fail) + ":" + e.message
            Log.i(TAG, s)
            callback2?.invoke(Exception(s))
        } finally {
            if (!lock.isLocked) {
                isConnected = false
                isConnecting = false
            }
        }
    }

    fun asciiToString(value: String): String {
        val sbu = StringBuffer()
        val chars = value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in chars.indices) {
            sbu.append(Integer.parseInt(chars[i]).toChar())
        }
        return sbu.toString()
    }

    @Synchronized
    override fun write(msg: String, callback: (Unit) -> Unit) {
        if (socket?.isConnected == true) {
            try {
                if (Looper.myLooper() == Looper.getMainLooper()) throw Exception(App.application.getString(R.string.ui_fail))
                isWriting = true
                socket?.getOutputStream()?.write(msg.toByteArray())
                socket?.getOutputStream()?.flush()
                callback.invoke(Unit)
            } catch (e: IOException) {
                Log.i(TAG, "发送失败:" + e.printStackTrace())
                isWriting = false
            }
        }
    }

    override fun read(): String? {
        var temp = bufferString;
        bufferString = null
        isWriting = false
        return temp
    }

    override fun addressEquals(other: NetAddress?): Boolean {
        return address != null && address == other
    }

    @Synchronized
    override fun isWriting(): Boolean {
        return isWriting
    }


    override fun isConnecting(): Boolean {
        return isConnecting
    }

    override fun isConnected(): Boolean {
        return isConnected
    }
}