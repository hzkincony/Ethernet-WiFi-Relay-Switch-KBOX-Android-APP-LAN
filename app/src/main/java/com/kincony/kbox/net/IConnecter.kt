package com.kincony.kbox.net

import com.kincony.kbox.net.data.NetAddress
import javax.security.auth.callback.Callback

interface IConnecter {
    fun connect()
    fun write(msg: String, callback: (Unit) -> Unit)
    fun read(): String? = null

    fun isWriting(): Boolean
    fun isConnecting(): Boolean
    fun isConnected(): Boolean
    fun addressEquals(other: NetAddress?): Boolean
}