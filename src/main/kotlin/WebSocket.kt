package sim868.kotlin

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI


class WebSocket(uri: URI) : WebSocketClient(uri) {
    init {
        super.connect()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        println(handshakedata)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println(code)
        println(reason)
        println(remote)
    }

    override fun onMessage(message: String?) {
        println(message)
    }


    override fun onError(ex: Exception?) {
        println(ex)
    }
}
//val wsClient = WebSocketClient(URI("ws://127.0.0.1"))
