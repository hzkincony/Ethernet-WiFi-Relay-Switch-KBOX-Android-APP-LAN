package com.kincony.kbox.net

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.kincony.kbox.App
import com.kincony.kbox.R
import com.kincony.kbox.net.data.NetAddress
import java.util.concurrent.Executors

/**
 * 帮助连接的类
 */
class Worker(val mHandler: Handler?) {
    private var executors = Executors.newCachedThreadPool()
    private var socketList = ArrayList<IConnecter>()

    fun sendCommand(address: NetAddress, command: String, callback: ((String) -> Unit)?, callback2: ((Exception) -> Unit)?) {
        var iConnecter: IConnecter? = null
        for (connecter in socketList) {
            if (connecter.addressEquals(address)) {
                iConnecter = connecter
                break
            }
        }

        if (iConnecter == null) {
            iConnecter = SocketConnecter(address, callback2)
            socketList.add(iConnecter)
        }

        executors.execute {
            connect(iConnecter)
        }

        executors.execute {
            waitForConnect(iConnecter)
            waitForWrite(iConnecter)
            iConnecter.write(command) {
                var read = waitForRead(iConnecter)
                mHandler?.post {
                    callback?.invoke(read)
                }
            }
        }
    }

    private fun connect(iConnecter: IConnecter) {
        if (!iConnecter.isConnecting()) {
            if (Looper.myLooper() == Looper.getMainLooper()) throw Exception(App.application.getString(R.string.ui_fail2))
            iConnecter.connect()
        }
    }

    private fun waitForConnect(iConnecter: IConnecter) {
        while (!iConnecter.isConnected()) {
            if (Looper.myLooper() == Looper.getMainLooper()) throw Exception(App.application.getString(R.string.ui_fail2))
            Thread.sleep(200)
            waitForConnect(iConnecter)
        }
    }


    private fun waitForWrite(iConnecter: IConnecter) {
        while (iConnecter.isWriting()) {
            if (Looper.myLooper() == Looper.getMainLooper()) throw Exception(App.application.getString(R.string.ui_fail2))
            Thread.sleep(200)
            waitForWrite(iConnecter)
        }
    }

    private fun waitForRead(iConnecter: IConnecter): String {
        var read: String?
        do {
            if (Looper.myLooper() == Looper.getMainLooper()) throw Exception(App.application.getString(R.string.ui_fail2))
            read = iConnecter.read()
            if (read == null) {
                Thread.sleep(100)
            }
        } while (read == null)
        return read
    }
}